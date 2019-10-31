package com.zanclick.prepay.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.authorize.vo.SettleDTO;
import com.zanclick.prepay.authorize.vo.SettleResult;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.RespInfo;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.SettleOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.SettleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
@Slf4j
@Service
public class PayOrderServiceImpl extends BaseMybatisServiceImpl<PayOrder, Long> implements PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private SettleOrderService settleOrderService;

    protected static ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected BaseMapper<PayOrder, Long> getBaseMapper() {
        return payOrderMapper;
    }

    @Override
    public PayOrder queryByOutTradeNo(String orderNo) {
        return payOrderMapper.selectByOutTradeNo(orderNo);
    }

    @Override
    public PayOrder queryByOutOrderNo(String outOrderNo) {
        return payOrderMapper.selectByOutOrderNo(outOrderNo);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handlePayOrder(PayOrder order) {
        if (order.isPayed()) {
            sendMessage(order);
        }
        this.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleSuccess(String outTradeNo) {
        PayOrder payOrder = payOrderMapper.selectByOutTradeNo(outTradeNo);
        if (payOrder == null) {
            log.error("交易订单异常:{}", outTradeNo);
            return;
        }
        payOrder.setState(PayOrder.State.payed.getCode());
        payOrder.setFinishTime(new Date());
        handlePayOrder(payOrder);
    }

    @Override
    public void sendMessage(PayOrder order) {
        String reason = sendSuccessMessage(order);
        if (reason == null) {
            settle(order.getRequestNo(), order.getOutTradeNo(), order.getSettleAmount(), order.getMerchantNo());
        } else {
            asyncSendMessage(order);
        }
    }


    private String sendSuccessMessage(PayOrder order) {
        String reason = null;
        AsiaInfoHeader header = AsiaInfoUtil.getHeader(order.getPhoneNumber());
        try {
            JSONObject object = new JSONObject();
            object.put("orderNo", order.getOutTradeNo());
            object.put("outOrderNo", order.getOutOrderNo());
            object.put("packageNo", order.getPackageNo());
            object.put("payTime", sdf.format(order.getFinishTime()));
            object.put("merchantNo", order.getMerchantNo());
            object.put("orderStatus", getOrderStatus(order.getState()));
            String result = RestHttpClient.post(header, object.toJSONString(), "commodity/freezenotify/v1.1.1");
            log.error("通知结果：{}", result);
            RespInfo info = JSONObject.parseObject(result, RespInfo.class);
            if (info.isSuccess()) {
                if (info.getResult().isSuccess()) {
                    return reason;
                } else {
                    log.error("通知出错:{}", info.getResult().getRetmsg());
                    return info.getResult().getRetmsg();
                }
            } else {
                log.error("通知出错:{}", info.getRespdesc());
                return info.getRespdesc();
            }
        } catch (Exception e) {
            log.error("通知出错:{}", e);
        }
        return "系统繁忙,请稍后再试";
    }


    static Long[] times = {10000L, 10000L, 10000L};

    private void asyncSendMessage(PayOrder order) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String reason = null;
                for (int i = 0; i < times.length; i++) {
                    try {
                        Thread.sleep(times[i]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    reason = sendSuccessMessage(order);
                    if (reason == null) {
                        settle(order.getRequestNo(), order.getOutTradeNo(),order.getSettleAmount(), order.getMerchantNo());
                    }
                }
                if (reason != null) {
                    createSettleOrder(order.getRequestNo(), reason, SettleOrder.State.notice_fail.getCode());
                }
            }
        });
    }


    /**
     * 创建各类型相关记录
     *
     * @param orderNo
     * @param state
     * @param reason
     */
    private void createSettleOrder(String orderNo, String reason, Integer state) {
        SettleOrder order = new SettleOrder();
        order.setCreateTime(new Date());
        order.setOrderNo(orderNo);
        order.setReason(reason);
        order.setState(state);
        settleOrderService.insert(order);
    }


    /**
     * 通知成功，开始结算
     *
     * @param amount
     * @param merchantNo
     * @param requestNo
     * @param outTradeNo
     */
    private void settle(String requestNo,String outTradeNo, String amount, String merchantNo) {
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(merchantNo);
        if (DateUtil.isSameDay(merchant.getCreateTime(), new Date())) {
            createSettleOrder(requestNo, null, SettleOrder.State.today_sign.getCode());
        } else {
            SettleDTO dto = new SettleDTO();
            dto.setAmount(amount);
            dto.setOutTradeNo(outTradeNo);
            SettleResult settleResult = authorizePayService.settle(dto);
            if (settleResult.isSuccess()) {
                createSettleOrder(requestNo, "等待结算", SettleOrder.State.settle_wait.getCode());
                RedisUtil.lSet("settle",requestNo);
            } else {
                createSettleOrder(requestNo, settleResult.getMessage(), SettleOrder.State.settle_fail.getCode());
            }
        }

    }

    /**
     * 获取交易状态
     *
     * @param state
     * @return
     */
    private String getOrderStatus(Integer state) {
        if (PayOrder.State.wait.getCode().equals(state)) {
            return "WAIT_PAY";
        } else if (PayOrder.State.payed.getCode().equals(state)) {
            return "TRADE_SUCCESS";
        } else if (PayOrder.State.refund.getCode().equals(state)) {
            return "TRADE_REFUND";
        }
        return "TRADE_CLOSED";
    }
}

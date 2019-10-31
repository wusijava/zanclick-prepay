package com.zanclick.prepay.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.RespInfo;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private AuthorizeOrderService authorizeOrderService;

    @Override
    protected BaseMapper<PayOrder, Long> getBaseMapper() {
        return payOrderMapper;
    }

    @Override
    public PayOrder queryByOrderNo(String orderNo) {
        return payOrderMapper.selectByOrderNo(orderNo);
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
    public void handleSuccess(AuthorizeOrder order) {
        authorizeOrderService.handleAuthorizeOrder(order);
        PayOrder payOrder = payOrderMapper.selectByOrderNo(order.getOrderNo());
        if (order == null) {
            log.error("交易订单异常:{}", order.getOrderNo());
            return;
        }
        order.setState(PayOrder.State.payed.getCode());
        order.setFinishTime(new Date());
        handlePayOrder(payOrder);
    }

    @Override
    public void sendMessage(PayOrder order) {
        AsiaInfoHeader header = AsiaInfoUtil.getHeader(order.getPhoneNumber());
        try {
            JSONObject object = new JSONObject();
            object.put("orderNo", order.getOrderNo());
            object.put("outOrderNo", order.getOutOrderNo());
            object.put("packageNo", order.getPackageNo());
            object.put("payTime", sdf.format(order.getFinishTime()));
            object.put("merchantNo", order.getMerchantNo());
            object.put("orderStatus", getOrderStatus(order.getState()));
            String result = RestHttpClient.post(header, object.toJSONString(), "commodity/freezenotify/v1.1.1");
            log.error("通知结果：{}", result);
            RespInfo info = JSONObject.parseObject(result,RespInfo.class);
            if (info.isSuccess()){
                if (info.getResult().isSuccess()){

                }else {
                    log.error("通知出错:{}",info.getResult().getRetmsg());
                }
            }else {
                log.error("通知出错:{}",info.getRespdesc());
            }
        } catch (Exception e) {
            log.error("通知出错:{}", e);
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

package com.zanclick.prepay.settle.listener;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.SettleDTO;
import com.zanclick.prepay.authorize.vo.SettleResult;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.RespInfo;
import com.zanclick.prepay.common.api.RestConfig;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.settle.entity.SettleOrder;
import com.zanclick.prepay.settle.service.SettleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 商户进阶通过，
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderNotifyListener {

    @Autowired
    private SettleOrderService settleOrderService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizePayService authorizePayService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @JmsListener(destination = JmsMessaging.ORDER_NOTIFY_MESSAGE)
    public void getMessage(String message) {
        PayOrder order = JSONObject.parseObject(message,PayOrder.class);
        SettleOrder settleOrder = getSettleOrder(order.getRequestNo());
        if (!settleOrder.getState().equals(SettleOrder.State.notice_fail.getCode()) && !settleOrder.getState().equals(SettleOrder.State.notice_wait.getCode())){
            return;
        }
        AsiaInfoHeader header = AsiaInfoUtil.header(getRouteValue(order));
        JSONObject object = new JSONObject();
        object.put("orderNo", order.getOutTradeNo());
        object.put("outOrderNo", order.getOutOrderNo());
        object.put("packageNo", order.getPackageNo());
        object.put("payTime", sdf.format(order.getFinishTime()));
        object.put("merchantNo", order.getMerchantNo());
        object.put("orderStatus", getOrderStatus(order.getState()));
        String result = RestHttpClient.post(header, object.toJSONString(), RestConfig.payOrderNotify);
        log.error("通知结果：{}", result);
        try {
            RespInfo info = JSONObject.parseObject(result, RespInfo.class);
            if (!info.isSuccess()) {
                log.error("能力系统异常:{},{}",order.getRequestNo(), info.getRespdesc());
                createSettleOrder(info.getRespdesc(),SettleOrder.State.notice_fail.getCode(),settleOrder.getId());
                return;
            }
            if (!info.getResult().isSuccess()){
                log.error("能力业务异常:{},{}",order.getRequestNo(), info.getResult().getRetmsg());
                createSettleOrder(info.getResult().getRetmsg(),SettleOrder.State.notice_fail.getCode(),settleOrder.getId());
                return;
            }
        }catch (Exception e){
            log.error("通知结果转换出错:{},{},{}",order.getRequestNo(),result, e);
            createSettleOrder("通知结果转换出错",SettleOrder.State.notice_fail.getCode(),settleOrder.getId());
            return;
        }
        settle(order.getOutTradeNo(),order.getSettleAmount(),order.getMerchantNo(),settleOrder.getId());
    }

    /**
     * 通知成功，开始结算
     *
     * @param amount
     * @param merchantNo
     * @param outTradeNo
     */
    private void settle(String outTradeNo, String amount, String merchantNo,Long id) {
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(merchantNo);
        if (DateUtil.isSameDay(merchant.getCreateTime(), new Date())) {
            createSettleOrder("当前签约，无法打款", SettleOrder.State.today_sign.getCode(),id);
        } else {
            SettleDTO dto = new SettleDTO();
            dto.setAmount(amount);
            dto.setOutTradeNo(outTradeNo);
            SettleResult settleResult = authorizePayService.settle(dto);
            if (settleResult.isSuccess()) {
                createSettleOrder("等待结算", SettleOrder.State.settle_wait.getCode(),id);
            } else {
                createSettleOrder(settleResult.getMessage(), SettleOrder.State.settle_fail.getCode(),id);
            }
        }

    }

    /**
     * 创建各类型相关记录
     *
     * @param state
     * @param reason
     */
    private void createSettleOrder(String reason, Integer state,Long id) {
        SettleOrder order = new SettleOrder();
        order.setId(id);
        order.setReason(reason);
        order.setState(state);
        settleOrderService.updateById(order);
    }

    private SettleOrder getSettleOrder(String orderNo) {
        SettleOrder order = settleOrderService.queryByOrderNo(orderNo);
        if (order == null){
            order = new SettleOrder();
            order.setCreateTime(new Date());
            order.setOrderNo(orderNo);
            order.setReason("等待通知");
            order.setState(SettleOrder.State.notice_wait.getCode());
            settleOrderService.insert(order);
        }
        return order;
    }


    /**
     * 获取路由类型
     *
     * @param order
     * @return
     */
    private String getRouteValue(PayOrder order) {
        String routeValue = null;
        if (RestConfig.isPhone()){
            routeValue = order.getPhoneNumber();
        }else {
            routeValue = order.getCity();
        }
        return routeValue;
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


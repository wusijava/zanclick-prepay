package com.zanclick.prepay.settle.listener;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 统一处理订单状态的改变（支付成功，关闭，退款等）
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderStateListener {

    @Autowired
    private PayOrderService payOrderService;

    static String outTradeNoKey = "outTradeNo";
    static String stateKey = "state";
    static String authNoKey = "authNo";

    @JmsListener(destination = JmsMessaging.ORDER_STATE_MESSAGE)
    public void getMessage(String message) {
        JSONObject object = new JSONObject();
        if (!object.containsKey(outTradeNoKey) || !object.containsKey(stateKey)){
            log.error("订单状态处理出错:{}",message);
            return;
        }
        String outTradeNo = object.getString(outTradeNoKey);
        String authNo = object.getString(authNoKey);
        Integer state = object.getInteger(stateKey);
        PayOrder order = payOrderService.queryByOutTradeNo(outTradeNo);
        if (order == null){
            log.error("订单编号错误:{}", outTradeNo);
            return;
        }
        if (order.isClosed()) {
            log.error("订单已关闭,处理异常:{}", message);
            return;
        }
        if (order.isPayed() && PayOrder.State.payed.getCode().equals(state)) {
            log.error("订单已交易完成无需再次处理:{}", outTradeNo);
            return;
        }
        if (order.isRefund() && PayOrder.State.refund.getCode().equals(state)){
            log.error("订单已退款完成无需再次处理:{}", outTradeNo);
            return;
        }
        if (order.isRefund() && PayOrder.State.payed.getCode().equals(state)){
            log.error("当前订单已退款，无法更改为支付成功，请确认:{}", message);
            return;
        }
        order.setState(state);
        if (order.isPayed() || order.isClosed()){
            order.setFinishTime(new Date());
            order.setAuthNo(authNo);
        }
        payOrderService.handlePayOrder(order);
    }

}


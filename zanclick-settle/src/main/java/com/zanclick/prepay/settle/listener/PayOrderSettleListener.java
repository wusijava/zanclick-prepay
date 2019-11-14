package com.zanclick.prepay.settle.listener;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 打款成功以及，账户结清通知
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderSettleListener {

    @Autowired
    private PayOrderService payOrderService;

    static String authNoKey = "authNo";
    static String stateKey = "state";

    @JmsListener(destination = JmsMessaging.ORDER_SETTLE_MESSAGE)
    public void getMessage(String message) {
        JSONObject object = JSONObject.parseObject(message);
        if (!object.containsKey(authNoKey) || !object.containsKey(stateKey)) {
            log.error("数据异常:{}", message);
            return;
        }
        Integer state = object.getInteger(stateKey);
        String authNo = object.getString(authNoKey);
        PayOrder order = payOrderService.queryByAuthNo(authNo);
        if (!order.getDealState().equals(PayOrder.DealState.settle_wait.getCode()) && !order.getDealState().equals(PayOrder.DealState.settled.getCode())) {
            log.error("结算状态异常:{},{},{}", authNo, order.getDealStateDesc(), message);
            return;
        }
        order.setDealState(state);
        order.setReason(object.getString("reason"));
        payOrderService.handleDealState(order);
    }
}


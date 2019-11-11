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
 * 商户进阶通过，
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderSettleListener {
    @Autowired
    private PayOrderService payOrderService;

    @JmsListener(destination = JmsMessaging.ORDER_SETTLE_MESSAGE)
    public void getMessage(String message) {
        JSONObject object = JSONObject.parseObject(message);
        if (!object.containsKey("authNo") || !object.containsKey("state")){
            log.error("数据异常:{}",message);
            return;
        }
        Integer state = object.getInteger("state");
        String authNo = object.getString("authNo");
        PayOrder order = payOrderService.queryByAuthNo(object.getString("authNo"));
        if (!order.getDealState().equals(PayOrder.DealState.settle_wait.getCode()) && !order.getDealState().equals(PayOrder.DealState.settled.getCode())){
            log.error("结算状态异常:{},{},{}",authNo,order.getDealStateDesc(),message);
            return;
        }
        order.setDealState(state);
        order.setReason(object.getString("reason"));
        payOrderService.updateById(order);
    }
}


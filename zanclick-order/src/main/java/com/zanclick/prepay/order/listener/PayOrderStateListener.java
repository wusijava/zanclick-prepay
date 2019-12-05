package com.zanclick.prepay.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private RedPacketService redPacketService;

    static String outTradeNoKey = "outTradeNo";
    static String stateKey = "state";
    static String authNoKey = "authNo";
    static String buyerNoKey = "buyerNo";

    @JmsListener(destination = JmsMessaging.ORDER_STATE_MESSAGE)
    public void getMessage(String message) {
        JSONObject object = JSONObject.parseObject(message);
        if (!object.containsKey(outTradeNoKey) || !object.containsKey(stateKey)){
            log.error("订单状态处理出错:{}",message);
            return;
        }
        String outTradeNo = object.getString(outTradeNoKey);
        String authNo = object.getString(authNoKey);
        Integer state = object.getInteger(stateKey);
        String buyerNo = object.getString(buyerNoKey);
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
            order.setBuyerNo(buyerNo);
        }
        payOrderService.handlePayOrder(order);
    }

    @JmsListener(destination = JmsMessaging.ORDER_REDPACKSTATE_MESSAGE)
    public void getSellerNoMessage(String message) {
        JSONObject object = JSONObject.parseObject(message);
        if (!object.containsKey("sellerNo") || !object.containsKey("redPackType")){
            log.error("红包结算类型处理出错:{}",message);
            return;
        }
        String sellerNo = object.getString("sellerNo");
        Integer redPackType = object.getInteger("redPackType");
        PayOrderQuery payOrderQuery = new PayOrderQuery();
        payOrderQuery.setSellerNo(sellerNo);
        payOrderQuery.setRedPackState(0);
        List<PayOrder> payOrders = payOrderService.queryList(payOrderQuery);
        if(DataUtil.isNotEmpty(payOrders)){
            PayOrder updateOrder = new PayOrder();
            updateOrder.setSellerNo(sellerNo);
            updateOrder.setRedPackType(redPackType);
            payOrderService.updateBySellerNo(updateOrder);
            redPacketService.updateTypeBySellerNo(sellerNo, redPackType);
        }

    }

}


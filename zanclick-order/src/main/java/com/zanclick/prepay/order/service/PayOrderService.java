package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
public interface PayOrderService extends BaseService<PayOrder, Long> {

    /**
     * 根据 orderNo查找
     *
     * @param outTradeNo
     * @return
     */
    PayOrder queryByOutTradeNo(String outTradeNo);

    /**
     * 根据 outOrderNo查找
     *
     * @param outOrderNo
     * @return
     */
    PayOrder queryByOutOrderNo(String outOrderNo);

    /**
     * 根据 outOrderNo查找
     *
     * @param outTradeNo
     * @param outOrderNo
     * @return
     */
    PayOrder queryAndHandlePayOrder(String outTradeNo, String outOrderNo);


    /**
     * 处理支付订单的问题
     *
     * @param order
     * @return
     */
    void handlePayOrder(PayOrder order);

    /**
     * 处理成功支付订单
     *
     * @param outTradeNo
     * @param authNo
     * @return
     */
    void handleSuccess(String outTradeNo,String authNo);


    /**
     * 向能力平台推送消息
     *
     * @param order
     * @return
     */
    void sendMessage(PayOrder order);

}

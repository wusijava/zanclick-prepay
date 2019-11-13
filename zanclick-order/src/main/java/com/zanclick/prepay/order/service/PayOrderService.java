package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.vo.RedPacketList;

import java.util.List;

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
     * 根据 authNo
     *
     * @param authNo
     * @return
     */
    PayOrder queryByAuthNo(String authNo);
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
     * 结算处理
     * @param outTradeNo
     *
     * */
    void settle(String outTradeNo);

    /**
     * 向能力平台推送消息
     *
     * @param order
     * @return
     */
    void sendMessage(PayOrder order);


    /**
     * 向能力平台推送消息
     *
     * @param outTradeNo
     * @param type 0 需要结清 1不需要结清
     * @return
     */
    void refund(String outTradeNo,Integer type);
    //按条件查找红包记录
    List<RedPacketList> redPacketList(PayOrderQuery query);
}

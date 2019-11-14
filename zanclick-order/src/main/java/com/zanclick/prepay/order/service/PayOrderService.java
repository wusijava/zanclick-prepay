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
     * @param outOrderNo
     * @return
     */
    PayOrder queryRedPacketOrder(String outOrderNo);
    /**
     * 查询并处理当前订单状态
     *
     * @param outTradeNo
     * @param outOrderNo
     * @return
     */
    PayOrder queryAndHandlePayOrder(String outTradeNo, String outOrderNo);


    /**
     * 处理订单状态问题
     *
     * @param order
     * @return
     */
    void handlePayOrder(PayOrder order);

    /**
     * 处理订单状态问题
     *
     * @param order
     * @return
     */
    void handleDealState(PayOrder order);


    /**
     * 同步查询一分钟(数据库里的状态与传入的状态不一样的时候停止)
     *
     * @param outTradeNo
     * @param dealState 原来的处理状态
     * @return
     */
    String syncQueryDealState(String outTradeNo,Integer dealState);

    /**
     * 同步查询一分钟(数据库里的状态与传入的状态一样的时候停止)
     *
     * @param outTradeNo
     * @param state 原来的处理状态
     * @return
     */
    String syncQueryState(String outTradeNo,Integer state);

    /**
     * 退款方法
     *
     * @param outOrderNo
     * @param outTradeNo
     * @return
     */
    String refund(String outTradeNo,String outOrderNo);
    /**
     * 退款方法
     *
     * @param order
     * @return
     */
    String refund(PayOrder order);
}

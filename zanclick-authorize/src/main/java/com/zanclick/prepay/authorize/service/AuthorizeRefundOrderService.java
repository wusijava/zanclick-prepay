package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeRefundOrder;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeRefundOrderService extends BaseService<AuthorizeRefundOrder, Long> {

    /**
     * 根据订单号查询退款订单
     *
     * @param requestNo
     * @return
     */
    AuthorizeRefundOrder queryByRequestNo(String requestNo);

    /**
     * 根据外部订单号查询退款订单
     *
     * @param outRequestNo
     * @return
     */
    AuthorizeRefundOrder queryByOutRequestNo(String outRequestNo);

    /**
     * 创建退款订单
     *
     * @param outRequestNo
     * @param orderNo
     * @param amount
     * @param reason
     *
     * @return
     */
    AuthorizeRefundOrder createRefundOrder(String amount, String orderNo,String outRequestNo, String reason);

    /**
     * 创建退款订单
     *
     * @param refund
     * @return
     */
    void refundFail(AuthorizeRefundOrder refund);


    /**
     * 创建退款订单
     *
     * @param refund
     *
     * @return
     */
    void refundSuccess(AuthorizeRefundOrder refund);
}

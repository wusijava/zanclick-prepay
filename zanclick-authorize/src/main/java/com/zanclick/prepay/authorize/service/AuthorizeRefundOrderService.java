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
     * 根据退款订单号查询退款订单
     *
     * @param refundNo
     * @return
     */
    AuthorizeRefundOrder queryByRefundNo(String refundNo);

    /**
     * 创建退款订单
     *
     * @param refundNo
     * @param orderNo
     * @param requestNo
     * @param amount
     * @param reason
     *
     * @return
     */
    AuthorizeRefundOrder createRefundOrder(String amount, String orderNo, String requestNo, String refundNo, String reason);

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

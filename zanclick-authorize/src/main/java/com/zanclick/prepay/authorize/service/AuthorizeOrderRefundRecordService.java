package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRefundRecord;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeOrderRefundRecordService extends BaseService<AuthorizeOrderRefundRecord, Long> {

    /**
     * 根据退款订单号查询退款订单
     *
     * @param refundNo
     * @return
     */
    AuthorizeOrderRefundRecord queryByRefundNo(String refundNo);

    /**
     * 创建退款订单
     *
     * @param refundNo
     * @param tradeNo
     * @param amount
     * @param reason
     * @return
     */
    AuthorizeOrderRefundRecord createRefundOrder(String amount, String tradeNo, String refundNo, String reason);

    /**
     * 创建退款订单
     *
     * @param refund
     * @return
     */
    void refundFail(AuthorizeOrderRefundRecord refund);


    /**
     * 创建退款订单
     *
     * @param refund
     * @return
     */
    void refundSuccess(AuthorizeOrderRefundRecord refund);
}

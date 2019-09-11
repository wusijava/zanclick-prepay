package com.zanclick.prepay.authorize.pay;

import com.zanclick.prepay.authorize.dto.*;

/**
 * 预授权支付
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizePayService {

    /**
     * 预支付
     *
     * @param dto
     * @return
     */
    PayResult prePay(PayDTO dto);


    /**
     * 结算
     *
     * @param dto
     * @return
     */
    SettleResult settle(SettleDTO dto);

    /**
     * 解冻操作
     *
     * @param dto
     * @return
     */
    FreezeResult unFreeze(UnFreezeDTO dto);

    /**
     * 支付查询
     *
     * @param dto
     * @return
     */
    QueryResult query(QueryDTO dto);

    /**
     * 支付查询
     *
     * @param dto
     * @return
     */
    QueryResult cancel(QueryDTO dto);


    /**
     * 支付退款(解冻操作，对应退款状态)
     *
     * @param dto
     * @return
     */
    RefundResult refund(RefundDTO dto);


    /**
     * 转支付退款（内部测试退款用的）
     *
     * @param dto
     * @return
     */
    PayRefundResult payRefund(PayRefundDTO dto);
    /**
     * 退款查询
     *
     * @param dto
     * @return
     */
    RefundResult refundQuery(RefundDTO dto);
}

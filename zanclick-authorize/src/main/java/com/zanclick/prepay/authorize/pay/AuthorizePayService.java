package com.zanclick.prepay.authorize.pay;

import com.zanclick.prepay.authorize.dto.*;
import com.zanclick.prepay.authorize.vo.*;

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
    PayResult prePay(AuthorizePay dto);


    /**
     * 结算
     *
     * @param dto
     * @return
     */
    SettleResult settle(Settle dto);

    /**
     * 退款操作（解冻/转支付）
     *
     * @param dto
     * @return
     */
    RefundResult refund(Refund dto);

    /**
     * 支付查询
     *
     * @param dto
     * @return
     */
    QueryResult query(Query dto);

    /**
     * 支付查询
     *
     * @param dto
     * @return
     */
    QueryResult cancel(Query dto);


    /**
     * 转支付退款
     *
     * @param dto
     * @return
     */
    PayRefundResult payRefund(PayRefund dto);
}

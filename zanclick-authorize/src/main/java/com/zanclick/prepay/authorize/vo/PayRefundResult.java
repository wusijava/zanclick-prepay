package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.authorize.vo.AuthorizeResponseParam;
import lombok.Data;

import java.util.Date;

/**
 * 支付数据封装
 *
 * @author duchong
 * @date 2019-7-5 10:32:07
 */
@Data
public class PayRefundResult extends AuthorizeResponseParam {

    /**
     * 订单号 （第三方产生）
     */
    private String outTradeNo;

    /**
     * 订单号 （自己生成）
     */
    private String orderNo;

    private String requestNo;

    private String outRequestNo;

    /**
     * 退款订单号 （第三方生成）
     */
    private String outRefundNo;

    private String refundNo;

    /**
     * 本次退款资金是否发生变化（Y/N）
     */
    private String isChange;

    /**
     * 退款订单金额
     */
    private String amount;

    /**
     * 退款金额
     */
    private String refundAmount;

    /**
     * 退款时间
     */
    private Date refundTime;
}

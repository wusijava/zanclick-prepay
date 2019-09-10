package com.zanclick.prepay.authorize.dto;

/**
 * @author lvlu
 * @date 2018-09-12 11:20
 **/
public class AliConstants {
    /**
     * 成功
     */
    public static final String SUCCESS = "10000";
    /**
     * 用户支付中
     */
    public static final String PAYING = "10003";
    /**
     * 失败
     */
    public static final String FAILED = "40004";
    /**
     * 系统异常
     */
    public static final String ERROR = "20000";

    /**
     * 花呗
     **/
    public static final String pcredit = "pcredit";

    /**
     * 花呗分期
     **/
    public static final String pcreditpayInstallment = "pcreditpayInstallment";


    public static final String disabled_channels = "coupon,promotion,voucher,point,mdiscount";

    /**
     * 交易创建，等待支付
     * */
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     * */
    public static final String TRADE_CLOSED = "TRADE_CLOSED";

    /**
     * 交易支付成功
     * */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";

    /**
     * 交易结束，不可退款
     * */
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
}

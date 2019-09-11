package com.zanclick.prepay.authorize.dto;

import lombok.Data;

/**
 * 支付数据封装
 *
 * @author duchong
 * @date 2019-7-5 10:32:07
 */
@Data
public class PayResult extends AuthorizeResponseParam {

    /**
     * 订单号（我方产生）
     * */
    private String tradeNo;

    /**
     * 订单号（第三方产生）
     * */
    private String outTradeNo;

    /**
     * 订单号（支付宝产生）
     * */
    private String thirdNo;

    /**
     * 二维码链接（支付宝产生）
     * */
    private String qrCodeUrl;

    private String eachMoney;

    private String firstMoney;
}

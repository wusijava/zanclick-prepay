package com.zanclick.prepay.authorize.vo;

import lombok.Data;

/**
 * 退款数据封装
 *
 * @author duchong
 * @date 2019-7-5 10:32:07
 */
@Data
public class QueryResult extends AuthorizeResponseParam {

    /**
     * 订单号 （第三方产生）
     */
    private String outTradeNo;

    /**
     * 订单号
     */
    private String requestNo;


    private String authNo;

}

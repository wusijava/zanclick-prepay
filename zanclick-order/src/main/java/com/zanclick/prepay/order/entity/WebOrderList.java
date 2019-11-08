package com.zanclick.prepay.order.entity;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class WebOrderList {

    private Long id;

    private String outTradeNo;

    private String outOrderNo;

    private String amount;

    private String settleAmount;

    private String createTime;

    private Integer state;

    private Integer dealState;

    private Integer num;

    private String title;

    private String merchantNo;

    private String wayId;

    private String storeName;

    private String sellerNo;
}

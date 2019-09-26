package com.zanclick.prepay.web.dto;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class QueryOrderResult {

    private String merchantNo;

    private String orderNo;

    private String outOrderNo;

    private String phoneNumber;

    private String orderTime;

    private String orderStatus;

    private String payTime;

    private String orderFee;

    private String packageNo;

}

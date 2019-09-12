package com.zanclick.prepay.web.dto;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class OrderList {

    private Long id;

    private String payTime;

    private String amount;

    private Integer state;

    private String stateDesc;

    private String merchantNo;

    private String storeNo;

    private String orderNo;
}

package com.zanclick.prepay.web.dto;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class QueryOrderStateResult {

    private String orderNo;

    private String outOrderNo;

    private String orderStatus;

    private String title;

    private String totalMoney;

    private String url;
}

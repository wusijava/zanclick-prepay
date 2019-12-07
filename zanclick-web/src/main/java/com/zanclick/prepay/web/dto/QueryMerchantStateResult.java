package com.zanclick.prepay.web.dto;

import lombok.Data;

@Data
public class QueryMerchantStateResult {

    /**
     * 标题
     */
    private String title;

    /**
     * 失败原因
     */
    private String msg;

}

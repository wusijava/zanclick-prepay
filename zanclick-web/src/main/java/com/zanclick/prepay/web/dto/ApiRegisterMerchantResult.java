package com.zanclick.prepay.web.dto;

import lombok.Data;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiRegisterMerchantResult{

    /**
     * 渠道标识
     */
    private String merchantNo;

    /**
     * 商户号
     */
    private String password;

}

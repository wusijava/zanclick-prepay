package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class VerifyMerchant extends Param {
    /**
     * 商户号
     */
    private String merchantNo;

    private String merchantName;

    private Integer registerStatus;

    private String managerName;

    private String managerMobile;

    private String wayid;

}

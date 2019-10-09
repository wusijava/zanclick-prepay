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
public class ApiVerifyMerchant extends Param {
    /**
     * 商户号
     */
    private String merchantNo;

    public String check() {
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        return null;
    }
}

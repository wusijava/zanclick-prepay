package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 商户信息验证
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class VerifyMerchant extends Param {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 加密信息
     */
    private String cipherJson;

    public String check() {
        if (checkNull(appId)) {
            return "缺少应用ID";
        }
        if (checkNull(cipherJson)){
            return "缺少业务参数";
        }
        return null;
    }
}

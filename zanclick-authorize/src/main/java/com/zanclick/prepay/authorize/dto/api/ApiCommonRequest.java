package com.zanclick.prepay.authorize.dto.api;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 网关请求必定会含有的两个参数
 * @author duchong
 * @date
 */
@Data
public class ApiCommonRequest extends Param {

    private String appId;

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

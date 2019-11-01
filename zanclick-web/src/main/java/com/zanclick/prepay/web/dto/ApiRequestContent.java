package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class ApiRequestContent extends Param {

    private String method;

    private String appId;

    private String cipherJson;

    private String content;

    public String check() {
        if (checkNull(method)) {
            return "缺少方法名";
        }
        if (checkNull(appId)) {
            return "缺少应用ID";
        }
        return null;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class ApiRequestContent {

    private String method;

    private String appId;

    private String cipherJson;

    private String content;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

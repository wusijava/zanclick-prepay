package com.zanclick.prepay.authorize.pay.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class AppAuthCode {

    private String grant_type = "authorization_code";

    private String code;


    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}

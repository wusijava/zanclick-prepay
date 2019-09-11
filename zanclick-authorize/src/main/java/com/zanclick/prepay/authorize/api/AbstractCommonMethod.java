package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;

/**
 * 预授权二维码支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
public abstract class AbstractCommonMethod {

    public <T> T parser(String content, Class<T> tClass) {
        return JSONObject.parseObject(content, tClass);
    }
}

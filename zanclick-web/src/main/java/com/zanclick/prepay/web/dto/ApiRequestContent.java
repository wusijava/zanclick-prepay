package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.Data;
import org.bouncycastle.jce.provider.symmetric.AES;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    public static void main(String[] args) {
        String encrypt = "WCQVr+86nB5IjcQMiGYuPatxs04okvrVXKIAIkfhjm6HG5m79C9OBwSEsQDNrK7BGiji7KfKSP9u757daDZVXQS9I3Op8M3w3j/oDHJyuTY=";
        ApiRequestContent content = new ApiRequestContent();
        content.setMethod("com.zanclick.query.auth.orderDetail");
        content.setAppId("201909101656231203575");
        content.setCipherJson(encrypt);
        content.setContent("{\"orderNo\":\"156826999643592106848\"}");

        System.err.println(AESUtil.Encrypt(content.toString()));

       StringBuffer sb = new StringBuffer();
       sb.append("appId=201909101656231203575&cipherJson=");
        String s = null;
        try {
            s = URLEncoder.encode(encrypt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append(s);
        System.err.println(sb.toString());
        System.err.println(AESUtil.Decrypt("0p8DmqWFTC/unjjMcH8cwdQrF4JsCueXDZnpiCfO23DSi053wuriy/OjGBLZzf3Xdg1wXtJRSq94PXc3MmSx0zwasnKMq3erXC8Q3zcgS1XiV6yvS7z80ezFXYpJ0PDgWtnXDVuXa8rEIb6GhFS/hr0ourhn9vOYGShCREC+cfbSMD6iHMx/4/w+IPtC+5HEdyBc8ESIsMDL8W9EHCezZHFD0+DHBxSdQS/P8l+FhN/QGFx9AWP9Kq9hn20uBLbqLg1ruC1KMmSkoBFKAxPF83LISqCF5WpYExzzqXlnq3Yodwtb8YdfpiKGaLsGhc3I7oATCa80DRedLu4fCngi8a0ZZCxGRwP/iDKcUwZyhsA="));
    }
}

package com.zanclick.prepay.authorize.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author lvlu
 * @date 2019/12/11 16:20
 */
@Slf4j
public class AlipayOauthUtil {

    public static final String OAUTH2_URL_BASE = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=auth_base&state=STATE&redirect_uri=ENCODED_URL";

    public static String getUserid(AlipayClient alipayClient,String code){
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(code);
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                return response.getUserId();
            }else {
                throw new RuntimeException("获取token失败");
            }
        }catch (Exception e){
            log.error("获取token失败");
            return null;
        }
    }

    public static String goToAuthUrlBase(String appid,String url,String param){
        try {
            return OAUTH2_URL_BASE.replace("APPID", appid).replace("STATE",param).replace("ENCODED_URL", URLEncoder.encode(url,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String goToAuthUrlBase(String appid,String url){
        try {
            return OAUTH2_URL_BASE.replace("APPID", appid).replace("STATE","").replace("ENCODED_URL", URLEncoder.encode(url,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

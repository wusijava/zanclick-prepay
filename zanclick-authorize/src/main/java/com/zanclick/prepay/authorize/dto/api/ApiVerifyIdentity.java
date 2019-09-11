package com.zanclick.prepay.authorize.dto.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.Data;

/**
 * 商户信息验证
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiVerifyIdentity extends ApiCommonRequest {

    public static void main(String[] args) {
//        JSONObject object = new JSONObject();
//        object.put("method","com.zanclick.verify.identity");
//        ApiRegisterMerchant merchant = new ApiRegisterMerchant();
//        merchant.setAppId("201909101656231203575");
//        JSONObject cipherJson = new JSONObject();
//        cipherJson.put("merchantNo","201909111510301201154724");
//        merchant.setCipherJson(AESUtil.Encrypt(cipherJson.toJSONString(),"12345678qwertyui"));
//        object.put("content",merchant);
//        System.err.println(object.toJSONString());
//        System.err.println(AESUtil.Encrypt(object.toJSONString()));

        ResponseParam param = new ResponseParam();
        param.setFail();
        param.setMessage("加密信息有误");
        System.err.println(AESUtil.Encrypt(param.toString()));
    }
}

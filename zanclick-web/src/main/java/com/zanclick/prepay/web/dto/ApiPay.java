package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.PayDTO;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 支付创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiPay extends PayDTO {

    @Override
    public String check() {
        return super.check();
    }

    public static void main(String[] args){
        JSONObject object = new JSONObject();
        object.put("method", "com.zanclick.create.auth.prePay");
        ApiPay dto = new ApiPay();
        dto.setMerchantNo("201909111719241201158791");
        dto.setDesc("交易描述");
        dto.setNum(0);
        dto.setAmount("100.00");
        dto.setOutOrderNo("123456789");
        dto.setPayWay(2);
        dto.setStoreNo("GZ785264135689");
        String encrypt = AESUtil.Encrypt(JSONObject.toJSONString(dto), "12345679qwertyui");

        StringBuffer sb = new StringBuffer();
        String s = null;
        try {
            s = URLEncoder.encode(encrypt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("appId=201909101656231203575&cipherJson=" + s);
        System.err.println(sb.toString());
    }
}

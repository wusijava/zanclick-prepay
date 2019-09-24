package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import com.zanclick.prepay.common.utils.AESUtil;
import com.zanclick.prepay.common.utils.StringUtils;
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
public class ApiPay extends Param {

    /**
     * 套餐编码
     */
    private String packageNo;

    /**
     * 交易订单号
     */
    private String outOrderNo;

    /**
     * 商户号
     */
    private String merchantNo;

    public String check() {
        if (checkNull(packageNo)) {
            return "缺少套餐编码";
        }
        if (checkNull(outOrderNo)) {
            return "缺少交易订单号";
        }
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        return null;
    }

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("method", "com.zanclick.create.auth.prePay");
        ApiPay dto = new ApiPay();
        dto.setMerchantNo("201909111719241201158791");
        dto.setOutOrderNo(StringUtils.getTradeNo());
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

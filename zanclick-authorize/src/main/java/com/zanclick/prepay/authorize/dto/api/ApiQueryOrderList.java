package com.zanclick.prepay.authorize.dto.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.PayDTO;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.Data;

/**
 * 商户信息验证
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiQueryOrderList extends ApiCommonRequest {

    /**
     * 分页参数
     */
    private Long nextIndex;

    @Override
    public String check() {

        return null;
    }

    public static void main(String[] args) {

        JSONObject object = new JSONObject();
        object.put("method","com.zanclick.query.auth.orderList");
        ApiQueryOrderList merchant = new ApiQueryOrderList();
        merchant.setAppId("201909101656231203575");
        merchant.setNextIndex(0L);
        JSONObject cipherJson = new JSONObject();
        cipherJson.put("merchantNo","201909111510301201154724");
        cipherJson.put("storeNo","123456789");
        merchant.setCipherJson(AESUtil.Encrypt(cipherJson.toJSONString(),"12345678qwertyui"));
        object.put("content",merchant);
        System.err.println(object.toJSONString());
        System.err.println(AESUtil.Encrypt(object.toJSONString()));
    }
}

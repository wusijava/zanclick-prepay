package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import com.zanclick.prepay.common.utils.AesUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiVerifyMerchant extends Param {
    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 渠道编号
     * */
    private String wayid;

    public String check() {
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        object.put("merchantNo","201909111719241201158791");
        String rs = AesUtil.Encrypt(object.toJSONString(),"dianzankeji09200");
        StringBuffer sb = new StringBuffer();
        String s = URLEncoder.encode(rs,"UTF-8");
        sb.append("appId=502004&cipherJson="+s);
        System.err.println(sb.toString());
    }
}

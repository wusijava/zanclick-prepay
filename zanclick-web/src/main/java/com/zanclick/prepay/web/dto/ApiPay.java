package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import com.zanclick.prepay.common.utils.AesUtil;
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

    private String phoneNumber;

    private String province;

    private String city;

    private String extJsonStr;

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
        if (checkNull(phoneNumber)) {
            return "缺少套餐号码";
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        object.put("merchantNo","201909111719241201158791");
        object.put("packageNo","NB5265897436");
        object.put("outOrderNo",StringUtils.getTradeNo());
        object.put("phoneNumber","13027129244");
        object.put("province","132");
        object.put("city","456");
        String rs = AesUtil.Encrypt(object.toJSONString(),"dianzankeji09200");
        StringBuffer sb = new StringBuffer();
        String s = URLEncoder.encode(rs,"UTF-8");
        sb.append("appId=502004&cipherJson="+s);
        System.err.println(sb.toString());
    }
}

package com.zanclick.prepay.authorize.dto.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import com.zanclick.prepay.common.utils.AESUtil;
import com.zanclick.prepay.common.utils.StringUtils;
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
public class ApiRegisterMerchant extends Param {
    /**
     * 支付宝实名名称
     */
    private String name;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 收款支付宝账号
     */
    private String sellerNo;


    /**
     * 门店名称
     * */
    private String storeName;

    /**
     * 营业执照名称
     * */
    private String storeSubjectName;

    /**
     * 营业执照编号
     * */
    private String storeSubjectCertNo;

    /**
     * 省
     * */
    private String storeProvince;
    private String storeProvinceCode;

    /**
     * 市
     * */
    private String storeCity;
    private String storeCityCode;

    /**
     * 区
     * */
    private String storeCounty;
    private String storeCountyCode;

    public String check() {
        if (checkNull(name)) {
            return "缺少支付宝实名名称";
        }
        if (checkNull(contactName)) {
            return "缺少联系人名称";
        }
        if (checkNull(contactPhone)) {
            return "缺少联系人电话";
        }
        if (checkNull(sellerNo)) {
            return "缺少收款支付宝账号";
        }
        if (checkNull(storeSubjectName)) {
            return "缺少营业执照名称";
        }
        if (checkNull(storeSubjectCertNo)) {
            return "缺少营业执照编号";
        }
        if (checkNull(storeProvince)) {
            return "缺少门店所属省份";
        }
        if (checkNull(storeCity)) {
            return "缺少门店所属城市";
        }
        if (checkNull(storeCounty)) {
            return "缺少门店所属区县";
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        JSONObject object = new JSONObject();
//        object.put("merchantNo",StringUtils.getMerchantNo());
//        object.put("storeNo","GZ785264135689");
//        System.err.println(AESUtil.Encrypt(object.toJSONString(),"12345679qwertyui"));

        StringBuffer sb = new StringBuffer();
        String s = URLEncoder.encode("WCQVr+86nB5IjcQMiGYuPatxs04okvrVXKIAIkfhjm6HG5m79C9OBwSEsQDNrK7BGiji7KfKSP9u757daDZVXQS9I3Op8M3w3j/oDHJyuTY=","UTF-8");
        sb.append("appId=201909101656231203575&cipherJson="+s);
        System.err.println(sb.toString());
    }
}

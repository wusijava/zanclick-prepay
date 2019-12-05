package com.zanclick.prepay.authorize.vo;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Param;
import com.zanclick.prepay.common.utils.StringUtils;
import lombok.Data;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class RegisterMerchant extends Param {

    private Integer index;

    /**
     * 渠道标识
     */
    private String wayId;

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
     * 平台ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 运营商名称
     */
    private String operatorName;

    /**
     * 门店编号
     */
    private String storeNo;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 营业执照名称
     */
    private String storeSubjectName;

    /**
     * 营业执照编号
     */
    private String storeSubjectCertNo;

    /**
     * 省
     */
    private String storeProvince;

    /**
     * 市
     */
    private String storeCity;

    /**
     * 区
     */
    private String storeCounty;

    private String storeCountyCode;

    private String storeCityCode;

    private String storeProvinceCode;

    private String createTime;

    private String state;

    private String reason;

    private String password;

    public String check() {
        if (checkNull(wayId)) {
            return "缺少渠道标识";
        }
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        if (checkNull(name)) {
            return "缺少支付宝实名名称";
        }
        if (checkNull(contactName)) {
            return "缺少联系人名称";
        }
        if (checkNull(contactPhone)) {
            return "缺少联系人电话";
        }
        if (!StringUtils.isPhone(contactPhone)) {
            return "联系人电话格式不正确";
        }
        if (checkNull(sellerNo)) {
            return "缺少收款支付宝账号";
        }
        if (!StringUtils.isPhone(sellerNo) && !StringUtils.isEmail(sellerNo)) {
            return "收款支付宝账号格式不正确";
        }
        if (checkNull(appId)) {
            return "缺少appId";
        }
        if (checkNull(operatorName)) {
            return "缺少运营商名称";
        }
        if (checkNull(storeNo)) {
            return "缺少缺少门店编号";
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

    public static String[] headers = {
            "序号",
            "渠道编码",
            "省份",
            "地区",
            "县（区）",
            "门店编号",
            "门店名称",
            "营业执照注册号",
            "营业执照名称",
            "联系人",
            "联系人电话",
            "支付宝认证姓名",
            "支付宝账号",
            "注册时间",
            "当前状态",
            "原因",
            "密码"
    };
    public static String[] keys = {
            "index",
            "wayId",
            "storeProvince",
            "storeCity",
            "storeCounty",
            "storeNo",
            "storeName",
            "storeSubjectCertNo",
            "storeSubjectName",
            "contactName",
            "contactPhone",
            "name",
            "sellerNo",
            "createTime",
            "state",
            "reason",
            "password"
    };

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("method", "com.zanclick.create.merchant");
    }
}

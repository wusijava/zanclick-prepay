package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class RegisterMerchant extends Param {

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
     * 收款支付宝ID
     */
    private String sellerId;

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
     * */
    private String operatorName;

    /**
     * 门店编号
     * */
    private String storeNo;

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

    /**
     * 市
     * */
    private String storeCity;

    /**
     * 区
     * */
    private String storeCounty;

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
        if (checkNull(sellerId)) {
            return "缺少收款支付宝ID";
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
            return "缺少门店背后主体名称";
        }
        if (checkNull(storeSubjectCertNo)) {
            return "缺少门店背后主体证件号";
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
}

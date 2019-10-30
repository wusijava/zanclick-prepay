package com.zanclick.prepay.authorize.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description
 * @date 2019-10-30 09:42:47
 */
@Data
public class SuppilerCreate {

    /**
     * 收款支付宝登录账号
     * */
    private String rcvLoginId;

    /**
     * 收款支付宝实名名称
     * */
    private String sellerName;

    /**
     * 收款人姓名
     * */
    private String rcvContactName;

    /**
     *收款人联系电话
     * */
    private String rcvContactPhone;

    /**
     * 收款人邮箱
     * */
    private String rcvContactEmail;

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
     * 门店营业执照名称
     * */
    private String storeSubjectName;

    /**
     * 门店营业执照编号
     * */
    private String storeSubjectCertNo;

    /**
     * 门店省
     * */
    private String storeProvince;

    /**
     * 门店市
     * */
    private String storeCity;

    /**
     * 门店区
     * */
    private String storeCounty;

}

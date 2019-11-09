package com.zanclick.prepay.authorize.vo.web;

import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class AuthorizeWebListInfo {

    private Long id;

    /**
     * 渠道标识
     */
    private String wayId;

    /**
     * 商户号
     */
    private String merchantNo;


    /**
     * 商户收款支付宝账号
     */
    private String sellerNo;


    /**
     * 支付宝实名名称
     **/
    private String name;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;


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



    /**
     * 记录状态
     **/
    private Integer state;

    private String stateStr;

    private String createTime;

    private String reason;
}

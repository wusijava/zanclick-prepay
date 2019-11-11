package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 预授权商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class MerchantDetail extends Param {
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
}

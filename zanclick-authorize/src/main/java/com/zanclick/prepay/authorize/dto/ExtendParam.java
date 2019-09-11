package com.zanclick.prepay.authorize.dto;

import lombok.Data;

/**
 * Created by Administrator on 2017/7/5.
 * 业务扩展参数
 *
 * @author duchong
 * @date 2019-7-9 11:03:26
 */
@Data
public class ExtendParam {
    /**
     * 资金授权业务对应的类目
     */
    private String secondaryMerchantId;
    /**
     * 使用花呗分期要进行的分期数
     */
    private String category;
    /**
     * 外部商户的门店编号
     */
    private String outStoreCode;
    /**
     * 外部商户的门店简称
     */
    private String outStoreAlias;

    private String sys_service_provider_id;

    private UnfreezeBizInfo unfreezeBizInfo;

}

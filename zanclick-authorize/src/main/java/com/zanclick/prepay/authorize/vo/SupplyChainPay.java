package com.zanclick.prepay.authorize.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description
 * @date 2019-10-30 09:42:47
 */
@Data
public class SupplyChainPay {

    /**
     * 预授权冻结时支付宝返回的28位预授权编号
     * */
    private String authNo;

    /**
     * 还款基恩
     * */
    private String payAmount;

}

package com.zanclick.prepay.authorize.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description
 * @date 2019-10-30 09:42:47
 */
@Data
public class SupplyChainCreate {

    /**
     * 预授权冻结时支付宝返回的28位预授权编号
     * */
    private String authNo;

    /**
     * 预授权顾客支付宝账户id
     * */
    private String freezeAliPayId;

    /**
     * 预授权外部订单号
     * */
    private String outMerOrderNo;

    /**
     * 预授权冻结时支付宝返回的operation_id
     * */
    private String operationId;

    /**
     * 预授权冻结时请求的out_request_no
     * */
    private String outRequestNo;

    /**
     * 冻结金额
     * */
    private String amount;

    /**
     * 期数
     * */
    private Integer fqNum;

    /**
     * 冻结开始时间
     * */
    private Date freezeDate;

    /**
     * 交易订单标题
     * */
    private Date expireDate;

    /**
     * 交易订单标题
     * */
    private String title;

    /**
     * 收款门店支付宝账号
     * */
    private String rcvLoginId;

    /**
     * 收款门店支付宝实名认证用户名
     * */
    private String rcvAliPayName;

    /**
     * 收款门店联系人
     * */
    private String rcvContactName;

    /**
     * 收款门店联系人电话
     * */
    private String rcvContactPhone;

    /**
     * 收款门店联系人邮箱
     * */
    private String rcvContactEmail;
}

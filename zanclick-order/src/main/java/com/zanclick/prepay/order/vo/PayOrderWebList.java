package com.zanclick.prepay.order.vo;

import lombok.Data;

/**
 * 资金授权订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class PayOrderWebList {

    private Long id;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 渠道编码
     */
    private String wayId;

    private String storeName;

    private String outTradeNo;

    /**
     * 外部编号
     **/
    private String outOrderNo;


    /**
     * 冻结金额
     */
    private String amount;

    /**
     * 结算金额
     */
    private String settleAmount;


    /**
     * 交易期数
     */
    private Integer num;

    /**
     * 套餐号码
     */
    private String phoneNumber;

    /**
     * 套餐标题
     */
    private String title;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 交易结束时间
     */
    private String finishTime;

    private Integer state;

    private String stateStr;

    private Integer dealState;
    private String dealStateStr;

    private String reason;

}

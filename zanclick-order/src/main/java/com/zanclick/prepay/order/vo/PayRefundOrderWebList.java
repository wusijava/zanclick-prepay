package com.zanclick.prepay.order.vo;

import lombok.Data;

/**
 * 资金授权订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class PayRefundOrderWebList {

    private Long id;

    /**
     * 商户号
     */
    private String wayId;

    /**
     * 渠道编码
     */
    private String authNo;

    private String outTradeNo;

    private String outOrderNo;

    private String createTime;

    private String finishTime;

    /**
     * 外部编号
     **/
    private String amount;

    private String redPacketAmount;

    /**
     * 冻结金额
     */
    private String stateDesc;

    private Integer state;

    /**
     * 结算金额
     */
    private String redPacketStateDesc;

    private Integer redPacketState;

    /**
     * 交易期数
     */
    private String repaymentStateDesc;

    private Integer repaymentState;

}

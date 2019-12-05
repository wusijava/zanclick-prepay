package com.zanclick.prepay.order.vo;

import lombok.Data;

@Data
public class PayRefundOrderExcelList {

    /**
     * 序号
     */
    private String no;

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
     * 交易时间
     */
    private  String dealTime;

    /**
     * 外部编号
     **/
    private String amount;

    private String redPacketAmount;


    private String sellerNo;

    private String sellerName;

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

    public static String[] headers = {
            "序号",
            "订单号",
            "外部订单号",
            "授权订单号",
            "渠道编码",
            "发起时间",
            "退款时间",
            "交易时间",
            "退款金额",
            "收款账号",
            "收款人",
            "红包金额",
            "退款状态",
            "红包状态",
            "垫资回款状态"
    };

    public static String[] keys = {
            "no",
            "outTradeNo",
            "outOrderNo",
            "authNo",
            "wayId",
            "createTime",
            "finishTime",
            "dealTime",
            "amount",
            "sellerNo",
            "sellerName",
            "redPacketAmount",
            "stateDesc",
            "redPacketStateDesc",
            "repaymentStateDesc"
    };

    public static void main(String[] args) {
        System.out.println(headers.length);
        System.out.println(keys.length);
    }
}

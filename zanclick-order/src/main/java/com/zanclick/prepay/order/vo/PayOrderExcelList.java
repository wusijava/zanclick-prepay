package com.zanclick.prepay.order.vo;

import lombok.Data;

/**
 * 资金授权订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class PayOrderExcelList {

    private Integer index;

    /**
     * 渠道编码
     */
    private String wayId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 订单号
     */
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
    private String num;

    /**
     * 套餐标题
     */
    private String title;

    /**
     * 套餐号码
     */
    private String phoneNumber;

    /**
     * 付款人
     */
    private String buyerNo;

    private String sellerNo;

    private String name;

    private String stateStr;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 交易结束时间
     */
    private String finishTime;


    private String province;

    private String city;

    private String county;

    private String redAmount;

    private String redState;

    public static String[] headers = {
            "序号",
            "渠道编码",
            "门店名称",
            "订单号",
            "外部订单号",
            "套餐金额",
            "结算金额",
            "捆绑期数",
            "套餐标题",
            "办理号码",
            "付款人",
            "收款账号",
            "收款人",
            "订单状态",
            "创建时间",
            "交易时间",
            "省",
            "市",
            "区",
            "红包金额",
            "红包状态"
    };
    public static String[] keys = {
            "index",
            "wayId",
            "storeName",
            "outTradeNo",
            "outOrderNo",
            "amount",
            "settleAmount",
            "num",
            "title",
            "phoneNumber",
            "buyerNo",
            "sellerNo",
            "name",
            "stateStr",
            "createTime",
            "finishTime",
            "province",
            "city",
            "county",
            "redAmount",
            "redState"
    };

    public static void main(String[] args) {
        System.err.println(headers.length);
        System.err.println(keys.length);
    }
}

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
    private String num;

    /**
     * 套餐标题
     */
    private String title;

    /**
     * 套餐号码
     */
    private String phoneNumber;

    private String stateStr;

    private String sellerNo;

    private String name;

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

    public static String[] headers = {"渠道编码","门店名称","订单号","外部订单号","套餐金额","结算金额","捆绑期数","套餐标题","办理号码","订单状态","收款账号","收款人","创建时间","交易时间","省","市","区"};
    public static String[] keys = {"wayId","storeName","outTradeNo","outOrderNo","amount","settleAmount","num","title","phoneNumber","stateStr","sellerNo","name","createTime","finishTime","province","city","county"};

    public static void main(String[] args) {
        System.err.println(headers.length);
        System.err.println(keys.length);
    }
}

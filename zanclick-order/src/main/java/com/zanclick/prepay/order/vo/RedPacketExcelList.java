package com.zanclick.prepay.order.vo;

import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/11/21 11:19
 * @Description 红包导出实体类
 **/
@Data
public class RedPacketExcelList {

    /**
     * 序号
     */
    private String no;
    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 外部订单号
     */
    private String outOrderNo;

    /**
     * 红包金额
     */
    private String amount;

    /**
     * 渠道编码
     */
    private String wayId;

    /**
     * 到账账号
     */
    private String receiveNo;

    /**
     * 领取时间
     */
    private String createTime;

    /**
     * 领取状态
     */
    private String state;

    /**
     * 原因
     */
    private String reason;

    public static String[] headers = {
            "序号",
            "订单号",
            "外部订单号",
            "红包金额",
            "渠道编码",
            "到账账号",
            "领取时间",
            "领取状态",
            "原因"
    };

    public static String[] keys = {
            "no",
            "outTradeNo",
            "outOrderNo",
            "amount",
            "wayId",
            "receiveNo",
            "createTime",
            "state",
            "reason"
    };


}

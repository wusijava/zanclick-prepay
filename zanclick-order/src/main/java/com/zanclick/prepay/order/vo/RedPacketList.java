package com.zanclick.prepay.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ Description   :  java类作用描述
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/13$ 13:09$
 */
@Data
public class RedPacketList  {
    //订单编号
    private String orderNo;
    //订单红包金额
    private String amount;
    //订单门店渠道编码
    private String wayId;
     //商户收款支付宝账号
    private String sellerNo;
    //红包领取时间
    private Date createTime;
    //红包状态
    private Integer state;






}

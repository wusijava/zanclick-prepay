package com.zanclick.prepay.order.vo;

import lombok.Data;

/**
 * @ Description   :  java类作用描述
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/13$ 13:09$
 */
@Data
public class RedPacketList  {

    private String outTradeNo;

    private String outOrderNo;

    private String amount;

    private String wayId;

    private String receiveNo;

    private String createTime;

    private Integer state;

    private String stateDesc;

    private String reason;

}

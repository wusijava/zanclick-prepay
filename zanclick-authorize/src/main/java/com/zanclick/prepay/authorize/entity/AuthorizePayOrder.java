package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019/12/9 18:13
 */
@Data
public class AuthorizePayOrder implements Identifiable<Long> {

    private Long id;

    private String authNo;

    private String productCode = "PRE_AUTH";

    private String tradeNo;

    private String aliTradeNo;

    private String totalAmount;

    private Integer state;

    private String buyerId;

    private String sellerId;

    private String subject;

    private String storeId;

    private String terminalId;

    private String body;

    private Date createTime;

    private Date finishTime;

}

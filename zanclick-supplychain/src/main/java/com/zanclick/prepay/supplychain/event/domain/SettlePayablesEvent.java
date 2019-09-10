package com.zanclick.prepay.supplychain.event.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019-05-10 12:13
 **/
@Data
public class SettlePayablesEvent extends BaseEvent {

    private String tradeNo;

    private Date clearDate;
}

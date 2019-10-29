package com.zanclick.prepay.authorize.event.domain;

import lombok.Data;

/**
 * @author lvlu
 * @date 2019-05-09 13:51
 **/
@Data
public class TradePayEvent extends BaseEvent {

    private String accountNo;

    private String accountType;

    private Long amount;
}

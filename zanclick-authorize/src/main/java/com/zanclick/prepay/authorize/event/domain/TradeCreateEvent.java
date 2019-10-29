package com.zanclick.prepay.authorize.event.domain;

import lombok.Data;

/**
 * @author lvlu
 * @date 2019-05-09 13:51
 **/
@Data
public class TradeCreateEvent extends BaseEvent {

    private String tradeAmount;

}

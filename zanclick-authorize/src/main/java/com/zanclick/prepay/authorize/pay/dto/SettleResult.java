package com.zanclick.prepay.authorize.pay.dto;

import com.zanclick.prepay.common.entity.ResponseParam;
import lombok.Data;

/**
 * 结算结果
 *
 * @author duchong
 * @date 2019-7-11 11:24:45
 */
@Data
public class SettleResult extends ResponseParam {

    private String tradeNo;

    private String outTradeNo;

}

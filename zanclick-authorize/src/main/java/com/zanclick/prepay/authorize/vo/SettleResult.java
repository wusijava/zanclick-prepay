package com.zanclick.prepay.authorize.vo;

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

    private String orderNo;

    private String outTradeNo;

}

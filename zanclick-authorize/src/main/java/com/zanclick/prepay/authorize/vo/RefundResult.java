package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.common.entity.ResponseParam;
import lombok.Data;

/**
 * 冻结处理结果
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class RefundResult extends ResponseParam {

    /**
     * 外部订单号（第三方产生）
     * */
    private String outTradeNo;

    /**
     * （解冻/转支付订单产生的）
     * */
    private String requestNo;

    private String outRequestNo;

}

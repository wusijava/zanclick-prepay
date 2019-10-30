package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 退款数据封装
 *
 * @author duchong
 * @date 2019-7-10 16:25:22
 */
@Data
public class PayRefundDTO extends RequestParam {

    /**
     * 预授权订单外部订单号
     */
    private String outTradeNo;

    /**
     * 预授权订单自己生成的订单号
     */
    private String tradeNo;

    /**
     * 转支付时生成的订单号
     */
    private String payTradeNo;

    /**
     * 退款订单号（可不传）
     * */
    private String refundNo;

    /**
     * 退款原因 可不传
     */
    private String reason;

    private String amount;

    @Override
    public String check() {
        if (checkNull(outTradeNo) && checkNull(tradeNo)) {
            return "请至少传入一个订单号";
        }
        if (checkNull(checkNull(payTradeNo))) {
            return "缺少入转支付订单号";
        }
        if (checkNull(checkNull(amount))) {
            return "缺少退款金额";
        }
        return null;
    }
}

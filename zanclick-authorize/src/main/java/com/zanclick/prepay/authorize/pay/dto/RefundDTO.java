package com.zanclick.prepay.authorize.pay.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 退款数据封装
 *
 * @author duchong
 * @date 2019-7-10 16:25:22
 */
@Data
public class RefundDTO extends RequestParam {

    /**
     * 订单号 （第三方产生）
     */
    private String outTradeNo;

    /**
     * 订单号 （自己生成 对应RequestNo）
     */
    private String tradeNo;

    /**
     * 退款订单号 （第三方生成）
     */
    private String refundNo;

    /**
     * 退款原因 可不传
     */
    private String reason;

    @Override
    public String check() {
        if (checkNull(outTradeNo) && checkNull(tradeNo)) {
            return "请至少传入一个订单号";
        }
        if (checkNull(checkNull(refundNo))) {
            return "缺少入退款订单号";
        }
        return null;
    }
}

package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 结算
 *
 * @author duchong
 * @date 2019-7-11 11:24:45
 */
@Data
public class SettleDTO extends RequestParam {
    /**
     * 订单号
     */
    private String requestNo;

    /**
     * 外部订单号
     */
    private String outTradeNo;

    private String amount;

    @Override
    public String check() {
        if (checkNull(requestNo) && checkNull(outTradeNo)) {
            return "请至少传入一个订单单号";
        }
        if (checkNull(amount)) {
            return "请传入结算金额";
        }
        return null;
    }
}

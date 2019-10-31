package com.zanclick.prepay.authorize.vo;

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
     * 转支付时生成的订单号
     */
    private String outRequestNo;

    /**
     * 退款订单号（可不传）
     * */
    private String requestNo;

    /**
     * 退款订单号
     * */
    private String outRefundNo;

    /**
     * 退款原因 可不传
     */
    private String reason;

    @Override
    public String check() {
        if (checkNull(outRequestNo) && checkNotNull(requestNo)) {
            return "缺少入转支付/解冻订单号";
        }
        if (checkNull(outRefundNo) ) {
            return "缺少退款订单号";
        }
        return null;
    }
}

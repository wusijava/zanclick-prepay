package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 支付查询
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class QueryDTO extends RequestParam {

    /**
     * 订单号（第三方提供）
     * */
    private String outTradeNo;

    /**
     * 订单号（对应订单里的requestNo）
     * */
    private String tradeNo;

    @Override
    public String check() {
        if (checkNull(outTradeNo) && checkNull(tradeNo)) {
            return "请至少传入一个订单号";
        }
        return null;
    }
}

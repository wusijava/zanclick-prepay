package com.zanclick.prepay.authorize.vo;

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
     * 订单号
     * */
    private String requestNo;

    @Override
    public String check() {
        if (checkNull(outTradeNo) && checkNull(requestNo)) {
            return "请至少传入一个订单号";
        }
        return null;
    }
}

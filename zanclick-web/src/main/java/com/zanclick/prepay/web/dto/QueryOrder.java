package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class QueryOrder extends Param {

    private String outOrderNo;

    /**
     * 对应 outTradeNo
     * */
    private String orderNo;

    public String check(){
        if (checkNull(outOrderNo) && checkNull(orderNo)){
            return "缺少交易订单号";
        }
        return null;
    }
}

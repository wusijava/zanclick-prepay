package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class QueryRedPacket extends Param {

    private String outOrderNo;

    public String check() {
        if (checkNull(outOrderNo)) {
            return "缺少交易订单号";
        }
        return null;
    }
}

package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class ReceiveRedPacket extends Param {

    private String outOrderNo;

    private String wayId;

    private String receiveNo;

    private String name;

    public String check() {
        if (checkNull(outOrderNo)) {
            return "缺少交易订单号";
        }
        if (checkNull(wayId)) {
            return "缺少渠道编码";
        }
        if (checkNull(receiveNo)) {
            return "缺少收款账号";
        }
        return null;
    }
}

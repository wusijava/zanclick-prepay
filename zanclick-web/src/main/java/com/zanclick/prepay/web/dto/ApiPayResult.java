package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

/**
 * 支付创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiPayResult extends Param {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 二维码链接
     */
    private String qrCodeUrl;
}

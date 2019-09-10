package com.zanclick.prepay.authorize.pay.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 支付创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class PayDTO extends RequestParam {
    /**
     * 商品描述
     */
    private String title;

    /**
     * 金额
     */
    private String amount;

    /**
     * 外部订单号（第三方产生）
     */
    private String outOrderNo;

    /**
     * 商户ID（第三方产生）
     */
    private String merchantNo;

    private String storeNo;

    /**
     * 支持的支付方式 (0-花呗 1-余额宝 2-全部)
     */
    private Integer payWay;

    @Override
    public String check() {
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        if (checkNull(outOrderNo)) {
            return "缺少外部订单号";
        }
        if (checkNull(amount)) {
            return "缺少交易金额";
        }

        if (checkMoneyFormat(amount)) {
            return "金额格式不正确";
        }

        if (checkMoney(amount, "1000000", "0.01")) {
            return "金额范围超出限制";
        }

        if (checkNull(title)) {
            return "缺少商品标题";
        }

        if (checkLength(title, 256)) {
            return "订单标题长度过长";
        }
        return null;
    }
}

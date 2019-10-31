package com.zanclick.prepay.authorize.vo;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 支付创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class AuthorizePay extends RequestParam {
    /**
     * 商品描述
     */
    private String desc;

    private Integer num;

    /**
     * 金额
     */
    private String amount;

    private String fee;

    /**
     * 外部订单号（第三方产生）
     */
    private String outTradeNo;

    /**
     * 商户ID（第三方产生）
     */
    private String merchantNo;

    /**
     * 支持的支付方式 (0-花呗 1-余额宝 2-全部)
     */
    private Integer payWay;

    @Override
    public String check() {
        if (checkNull(merchantNo)) {
            return "缺少商户号";
        }
        if (checkNull(outTradeNo)) {
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
        if (checkNull(desc)){
            return "缺少商品描述";
        }
        if (checkLength(desc, 256)) {
            return "商品描述过长";
        }
        if (this.payWay == null){
            this.payWay = 2;
        }
        if (this.num == null){
            this.num = 0;
        }
        return null;
    }

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("method","com.zanclick.create.auth.prePay");
        System.err.println(object.toJSONString());
    }
}

package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 解冻/转支付
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class Refund extends RequestParam {

    /**
     * 解冻/转支付 的金额
     * */
    private String amount;

    /**
     * 外部订单号（第三方产生）
     * */
    private String outTradeNo;

    /**
     * 订单号（自己产生）
     */
    private String orderNo;

    /**
     * 解冻/转支付 订单号（自己生成，对应本次操作）
     * */
    private String outRequestNo;

    /**
     * 操作 0解冻 1转支付
     * */
    private Integer type;

    /**
     * 解冻描述(按期还款，逾期转支付等)
     * */
    private String reason;

    @Override
    public String check() {
        if (checkNull(outTradeNo) && checkNull(orderNo)) {
            return "请至少选择一个订单号";
        }
        if (checkNull(outRequestNo)) {
            return "缺少外部请求号";
        }
        if (checkNull(type)){
            return "缺少操作类型";
        }
        if (checkEquals(type,0,1)){
            return "操作类型错误";
        }
        if (checkNull(reason)){
            return "缺少交易描述";
        }
        if (checkNull(amount)){
            return "缺少金额信息";
        }
        if (checkMoneyFormat(amount)){
            return "请填写正确的金额信息";
        }
        if (checkMoney(amount,"1000000","0.01")){
            return "金额范围需在0.01~1000000之间";
        }
        return null;
    }

    public boolean isUnFree(){
        return type.equals(0);
    }

    public boolean isPay(){
        return type.equals(1);
    }
}

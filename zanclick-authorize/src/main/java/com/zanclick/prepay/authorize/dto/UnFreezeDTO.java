package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 支付创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class UnFreezeDTO extends RequestParam {

    /**
     * 期数(必传 0全部解冻)
     * */
    private Integer num;

    /**
     * 外部订单号（第三方产生）
     * */
    private String outTradeNo;

    /**
     * 订单号（自己产生）
     */
    private String tradeNo;

    /**
     * 订单号（自己生成，对应本次操作）
     * */
    private String orderNo;

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
        if (checkNull(outTradeNo) && checkNull(tradeNo)) {
            return "请至少选择一个订单号";
        }
        if (checkNull(orderNo)) {
            return "缺少操作流水号";
        }
        if (checkNull(num)) {
            return "缺少操作期数";
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
        return null;
    }

    public boolean isUnFree(){
        return type.equals(0);
    }

    public boolean isPay(){
        return type.equals(1);
    }
}

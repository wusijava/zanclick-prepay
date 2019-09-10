package com.zanclick.prepay.supplychain.enums;

import com.zanclick.prepay.common.enums.BaseEnum;

/**
 * @author lvlu
 * @date 2019-07-04 17:18
 **/
public enum TradeStateEnum implements BaseEnum<Integer> {
    CREATED(0,"交易已创建"),FAILED(-2,"交易创建失败"),CANCELED(-1,"已取消"),
    WAIT_RECEIPT(1,"等待放款"),TOCANCEL(2,"待取消"),LOANED(3,"已打款"),FINISHED(4,"已结清");
    private Integer code;
    private String desc;

    TradeStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}

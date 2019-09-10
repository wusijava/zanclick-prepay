package com.zanclick.prepay.supplychain.enums;

import com.zanclick.prepay.common.enums.BaseEnum;

/**
 * @author lvlu
 * @date 2019-07-04 17:18
 **/
public enum BillStateEnum implements BaseEnum<Integer> {
    CREATED(0, "交易已创建"), FAILED(-1, "异步通知失败"), UNCREATED(-2, "直接返回失败"),
    SUCC(1, "交易创建成功");
    private Integer code;
    private String desc;

    BillStateEnum(Integer code, String desc) {
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

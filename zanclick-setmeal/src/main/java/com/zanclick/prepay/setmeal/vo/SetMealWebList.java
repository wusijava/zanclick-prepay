package com.zanclick.prepay.setmeal.vo;

import lombok.Data;

@Data
public class SetMealWebList {

    /**
     * 套餐编号
     */
    private String packageNo;




    /**
     * 总冻结金额
     */
    private String totalAmount;

    private String settleAmount;

    /**
     * 分期期数
     **/
    private Integer num;

    /**
     * 每期金额
     **/
    private String amount;



    /**
     * 标题
     */
    private String title;

    /**
     * 状态
     */
    private Integer state;

    private String stateStr;
}

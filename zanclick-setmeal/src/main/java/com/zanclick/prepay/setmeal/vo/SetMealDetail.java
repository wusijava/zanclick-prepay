package com.zanclick.prepay.setmeal.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author duchong
 * @description 套餐列表描述
 * @date 2019-12-2 17:37:23
 */
@Data
public class SetMealDetail {

    private Long id;

    @ApiModelProperty("套餐标题")
    private String title;

    @ApiModelProperty("套餐金额")
    private String totalAmount;

    @ApiModelProperty("结算金额")
    private String settleAmount;

    @ApiModelProperty("捆绑期数")
    private Integer num;

    @ApiModelProperty("红包状态 0关闭的 1开启的")
    private Integer redPacketState;

    @ApiModelProperty("红包状态红包状态描述")
    private String redPacketStateStr;

    @ApiModelProperty("红包金额")
    private String redPacketAmount;

    @ApiModelProperty("套餐状态 0已下架 1已上架")
    private Integer state;

    @ApiModelProperty("套餐状态描述")
    private String stateStr;

    @ApiModelProperty("每期应还")
    private String eachAmount;
}

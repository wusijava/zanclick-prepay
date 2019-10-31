package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 结算订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class SettleOrder implements Identifiable<Long> {

    private Long id;

    /**
     * 结算状态
     */
    private Integer state;

    private String orderNo;

    private Date createTime;

}

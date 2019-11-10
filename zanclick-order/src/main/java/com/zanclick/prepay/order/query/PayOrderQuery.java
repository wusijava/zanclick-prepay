package com.zanclick.prepay.order.query;

import com.zanclick.prepay.order.entity.PayOrder;
import lombok.Data;

/**
 * 资金授权订单
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class PayOrderQuery extends PayOrder {

    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;

}

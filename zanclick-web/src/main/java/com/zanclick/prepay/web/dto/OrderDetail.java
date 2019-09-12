package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.KeyValue;
import lombok.Data;

import java.util.List;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class OrderDetail {

    private Long id;

    private String amount;

    private Integer state;

    private String stateDesc;

    private String orderNo;

    private Integer refund;

    private List<KeyValue> list;
}

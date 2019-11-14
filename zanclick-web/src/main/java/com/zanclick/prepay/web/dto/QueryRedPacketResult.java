package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.KeyValue;
import lombok.Data;

import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:10
 */
@Data
public class QueryRedPacketResult {

    private List<KeyValue> valueList;

    private String outOrderNo;

    private String amount;
}

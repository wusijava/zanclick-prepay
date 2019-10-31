package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 资金授权商户
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class SettleRate implements Identifiable<Long> {

    private Long id;

    /**
     * 费率
     */
    private String rate;

    private String appId;

}

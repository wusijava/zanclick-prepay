package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 资金授权订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeOrderFee implements Identifiable<Long> {

    private Long id;

    /**
     * 冻结金额
     **/
    private String money;

    /**
     * 服务费
     **/
    private String serviceMoney;

    private String orderRealMoney;

    /**
     * 最后一期应该还的
     */
    private String firstMoney;

    /**
     * 每期应还
     */
    private String eachMoney;

    /**
     * 结算周期（这里只能按月）
     **/
    private Integer cycle;

    private String orderNo;

    private String requestNo;

}

package com.zanclick.prepay.authorize.query;

import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import lombok.Data;

/**
 * 资金授权订单
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeOrderQuery extends AuthorizeOrder {

    private Integer page;

    private Integer limit;

    private Integer offset;

    private Long nextIndex;

    private String stateIn;

}

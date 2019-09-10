package com.zanclick.prepay.authorize.pay.dto;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-7-11 11:17:06
 */
@Data
public class AuthorizeResponseParam extends ResponseParam {

    /**
     * 订单状态
     */
    private Integer state = 100;

    public boolean isUnpayed() {
        return AuthorizeOrder.State.unPay.getCode().equals(state);
    }

    public boolean isClosed() {
        return AuthorizeOrder.State.closed.getCode().equals(state);
    }

    public boolean isPayed() {
        return AuthorizeOrder.State.payed.getCode().equals(state);
    }

    public boolean isPaying() {
        return AuthorizeOrder.State.paying.getCode().equals(state);
    }

    public boolean isRefund() {
        return AuthorizeOrder.State.refund.getCode().equals(state);
    }
}

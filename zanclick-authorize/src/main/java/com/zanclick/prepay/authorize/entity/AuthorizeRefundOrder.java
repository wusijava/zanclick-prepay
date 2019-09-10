package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeRefundOrder implements Identifiable<Long> {

    private Long id;

    private String requestNo;

    private String amount;

    private String orderNo;

    private String refundNo;

    /**
     * 退款原因
     * */
    private String refundReason;

    /**
     * 退款失败原因
     * */
    private String reason;

    private Date createTime;

    private Date finishTime;

    /**
     * 退款状态 0退款中 -1退款失败 1退款成功
     * */
    private Integer state;


    public Boolean isSuccess(){
        return state.equals(1);
    }

    public Boolean isFail(){
        return state.equals(-1);
    }

}

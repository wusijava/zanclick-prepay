package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 预期还款计划
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeOrderRefundRecord implements Identifiable<Long> {

    private Long id;

    /**
     * 授权码
     */
    private Date createTime;

    private Date finishTime;

    private String refundReason;

    private String reason;

    private String amount;

    private String tradeNo;

    private String refundNo;

    private Integer state;

    public Boolean isSuccess(){
        return State.success.getCode().equals(state);
    }

    public Boolean isFail(){
        return State.fail.getCode().equals(state);
    }

    public Boolean isWait(){
        return State.wait.getCode().equals(state);
    }

    public enum State {
        /**
         * 退款
         */
        wait(0),
        /**
         * 退款成功
         */
        success(1),
        /**
         * 退款失败
         */
        fail(-1);


        private Integer code;

        State(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

}

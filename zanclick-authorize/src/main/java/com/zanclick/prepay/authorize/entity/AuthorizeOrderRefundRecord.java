package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 转支付退款记录
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeOrderRefundRecord implements Identifiable<Long> {

    private Long id;

    private Date createTime;

    private Date finishTime;

    private String refundReason;

    private String reason;

    private String amount;

    private String requestNo;

    private String refundNo;

    private String outRefundNo;

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
         * 等待退款
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

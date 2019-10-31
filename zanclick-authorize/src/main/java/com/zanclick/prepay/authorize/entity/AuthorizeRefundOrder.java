package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 转支付/解冻记录
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeRefundOrder implements Identifiable<Long> {

    private Long id;

    private String amount;

    private Integer type;

    private String authNo;

    private String outRequestNo;

    private String requestNo;

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

    public enum Type {
        UN_FREEZE(0, "解冻"),
        PAY(1, "转支付");

        private Integer code;
        private String desc;

        Type(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public enum State {
        success(1, "成功"),
        fail(-1, "失败"),
        refund(2, "退款"),
        wait(0, "等待");

        private Integer code;
        private String desc;

        State(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public Boolean isSuccess(){
        return State.success.getCode().equals(state);
    }

    public Boolean isFail(){
        return State.fail.getCode().equals(state);
    }


    public Boolean isWait(){
        return State.wait.getCode().equals(state);
    }

}

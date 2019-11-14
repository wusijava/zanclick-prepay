package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 资金授权商户
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class PayRefundOrder implements Identifiable<Long> {

    private Long id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String merchantNo;

    private String wayId;

    private String outTradeNo;

    private String outOrderNo;

    private String outRequestNo;

    private String amount;

    private String redPackAmount;

    private Date createTime;

    /**
     * 退款状态
     */
    private Integer state;

    /**
     * 红包状态
     */
    private String redPackState;

    /**
     * 贷款状态
     */
    private Integer repaymentState;


    public enum RedPackState {
        receive(1, "未退还"),
        un_receive(0, "未领取"),
        refund(2,"已退还");

        private Integer code;
        private String desc;

        RedPackState(Integer code, String desc) {
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

    public enum RepaymentState {
        no_need_paid(2, "无需打款"),
        paid(1, "已打款"),
        no_paid(2, "等待打款");

        private Integer code;
        private String desc;

        RepaymentState(Integer code, String desc) {
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
        wait(0, "等待退款"),
        closed(-1, "退款失败"),
        success(1, "退款成功");

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

    public String getStateDesc() {
        if (State.closed.getCode().equals(state)) {
            return State.closed.getDesc();
        } else if (State.wait.getCode().equals(state)) {
            return State.wait.getDesc();
        } else {
            return State.success.getDesc();
        }
    }

    public String getRedPacketDesc() {
        if (RedPackState.un_receive.getCode().equals(redPackState)) {
            return RedPackState.un_receive.getDesc();
        } else if (RedPackState.receive.getCode().equals(redPackState)) {
            return RedPackState.receive.getDesc();
        } else{
            return RedPackState.refund.getDesc();
        }
    }

    public String getRepaymentStateDesc() {
        if (RepaymentState.paid.getCode().equals(repaymentState)) {
            return RepaymentState.paid.getDesc();
        } else if (RepaymentState.no_need_paid.getCode().equals(repaymentState)) {
            return RepaymentState.no_need_paid.getDesc();
        } else{
            return RepaymentState.no_paid.getDesc();
        }
    }
}

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
public class PayOrder implements Identifiable<Long> {

    private Long id;

    /**
     *
     */
    private String packageNo;

    /**
     *
     */
    private String appId;

    /**
     *
     */
    private String merchantNo;

    /**
     * 分期期数
     **/
    private String orderNo;

    /**
     * 每期金额
     **/
    private String outOrderNo;

    private String phoneNumber;

    private String province;

    private String city;

    /**
     * 冻结金额
     * */
    private String amount;

    private String title;

    private Integer num;

    /**
     * 标题
     */
    private Date createTime;

    private Date finishTime;

    private Integer state;

    public enum State {
        wait(0, "等待支付"),
        closed(-1, "关闭的"),
        payed(1,"已支付");

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

    public Boolean isPayed() {
        return State.payed.getCode().equals(state);
    }

    public Boolean isClosed() {
        return State.closed.getCode().equals(state);
    }

    public Boolean isWait() {
        return State.wait.getCode().equals(state);
    }
}

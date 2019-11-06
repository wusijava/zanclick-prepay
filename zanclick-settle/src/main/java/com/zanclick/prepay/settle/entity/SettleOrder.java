package com.zanclick.prepay.settle.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 结算订单
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class SettleOrder implements Identifiable<Long> {

    private Long id;

    /**
     * 结算状态
     */
    private Integer state;

    private String orderNo;

    private Date createTime;

    private String reason;

    public enum State {
        notice_wait(4, "等待通知"),
        notice_fail(0, "通知失败"),
        settle_fail(1, "结算失败"),
        settle_wait(2, "等待结算"),
        today_sign(3, "当天签约的");

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

    public static void main(String[] args) {
        System.err.println("CDZHA20505\n");
        System.err.println("CDZHA20505");
    }

}

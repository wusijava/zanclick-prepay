package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class RedPacket implements Identifiable<Long> {

    private Long id;

    private String amount;

    private String outOrderNo;

    private String outTradeNo;

    private String payNo;

    private String bizNo;

    private String title;

    private Date createTime;

    private String receiveNo;

    private String appId;

    private Integer state;

    private String reason;

    private String wayId;

    private String merchantNo;

    private String name;

    public enum State {
        failed(-1, "领取失败"),
        waiting(0, "等待领取"),
        success(1, "领取成功");

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


    public String getStateDesc(){
        if (State.success.getCode().equals(state)){
            return State.success.getDesc();
        }  else if (State.waiting.getCode().equals(state)){
            return State.waiting.getDesc();
        }else {
            return State.failed.getDesc();
        }
    }
}

package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class RedPacket implements Identifiable<Long> {

    private Long id;

    private String amount;

    private String title;

    private String outOrderNo;

    private String outTradeNo;

    private Date createTime;

    private Date finishTime;

    private String appId;

    private Integer state;

    private Integer type;

    private String wayId;

    private String merchantNo;

    private String sellerNo;

    private String name;

    public enum State {
        refund(2, "红包退还"),
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

    public enum Type {
        personal(0, "个人"),
        settleDay(1, "日结"),
        settleWeek(2, "周结"),
        settleMonth(3, "月结");

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

    public String getTypeDesc(){
        if (Type.personal.getCode().equals(type)){
            return Type.personal.getDesc();
        }  else if (Type.settleDay.getCode().equals(type)){
            return Type.settleDay.getDesc();
        }  else if (Type.settleWeek.getCode().equals(type)){
            return Type.settleWeek.getDesc();
        }else {
            return Type.settleMonth.getDesc();
        }
    }

    public String getStateDesc(){
        if (State.success.getCode().equals(state)){
            return State.success.getDesc();
        }  else if (State.waiting.getCode().equals(state)){
            return State.waiting.getDesc();
        }else {
            return State.refund.getDesc();
        }
    }
}

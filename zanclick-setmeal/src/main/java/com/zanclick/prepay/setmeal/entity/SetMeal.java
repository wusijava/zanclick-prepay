package com.zanclick.prepay.setmeal.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 资金授权商户
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class SetMeal implements Identifiable<Long> {

    private Long id;

    /**
     * 套餐编号
     */
    private String packageNo;

    /**
     * 电渠商品标识
     */
    private String tradeMark;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 总冻结金额
     */
    private String totalAmount;

    private String settleAmount;

    /**
     * 分期期数
     **/
    private Integer num;

    /**
     * 每期金额
     **/
    private String amount;

    private String redPackAmount;

    private Integer redPackState;

    /**
     * 标题
     */
    private String title;

    private Integer state;

    public String getRedPacketStateStr(){
        if (RedPackState.open.getCode().equals(redPackState)){
            return RedPackState.open.getDesc();
        }else {
            return RedPackState.closed.getDesc();
        }
    }

    public String getStateStr(){
        if (State.open.getCode().equals(state)){
            return State.open.getDesc();
        }else {
            return State.closed.getDesc();
        }
    }


    public enum RedPackState {
        open(1, "开启"),
        closed(0, "关闭的");

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

    public enum State {
        open(1, "已上架"),
        closed(0, "已下架");

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
        if(State.open.getCode().equals(state)){
            return "已上架";
        }
        else {
            return "已下架";
        }
    }

    public Boolean isOpen() {
        return state.equals(State.open.getCode());
    }

    public Boolean isClosed() {
        return state.equals(State.closed.getCode());
    }
}

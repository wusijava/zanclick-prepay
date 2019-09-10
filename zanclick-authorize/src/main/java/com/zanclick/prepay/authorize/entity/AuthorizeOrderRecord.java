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
public class AuthorizeOrderRecord implements Identifiable<Long> {

    private Long id;

    /**
     * 授权码
     */
    private String authNo;

    private String tradeNo;

    private String money;

    private Integer num;

    private Date createTime;

    /**
     * 预期结算时间
     */
    private String expectTime;

    /**
     * 真实还款时间
     */
    private Date realTime;

    private Integer state;

    public Boolean isWait(){
        return state.equals(State.wait.getCode());
    }

    public Boolean isUnFreeze(){
        return state.equals(State.unfreed.getCode());
    }

    public Boolean isPayed(){
        return state.equals(State.payed.getCode());
    }

    public enum State {
        /**
         * 等待结算
         */
        wait(0),
        /**
         * 解冻成功
         */
        unfreed(1),
        /**
         * 转支付
         */
        payed(2),
        /**
         * 转支付退款
         */
        refund(3);


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

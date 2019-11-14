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
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String merchantNo;

    private String storeName;

    private String wayId;

    /**
     * 套餐编码
     */
    private String packageNo;

    /**
     * 订单编号
     **/
    private String outTradeNo;

    private String requestNo;

    /**
     * 外部编号
     **/
    private String outOrderNo;

    private String authNo;

    /**
     * 冻结金额
     */
    private String amount;

    /**
     * 结算金额
     */
    private String settleAmount;

    /**
     * 每期应还金额
     */
    private String eachMoney;

    /**
     * 首期应还金额
     */
    private String firstMoney;

    /**
     * 交易期数
     */
    private Integer num;

    /**
     * 套餐号码
     */
    private String phoneNumber;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 套餐标题
     */
    private String title;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 交易结束时间
     */
    private Date finishTime;

    private String qrCodeUrl;

    private Integer state;

    private Integer dealState;

    private Integer redPackState;

    private String redPackAmount;

    private String redPackSellerNo;

    private String name;

    private String sellerNo;

    private String provinceName;

    private String cityName;

    private String districtName;

    private String reason;

    private String buyerNo;

    public enum RedPackState {
        receive(1, "已领取"),
        un_receive(0, "未领取");

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

    public enum DealState {
        notice_wait(4, "等待通知"),
        notice_fail(0, "通知失败"),
        settle_wait(2, "等待打款"),
        settle_fail(1, "打款失败"),
        settled(5, "打款成功"),
        repayment_success(6, "还款成功"),
        today_sign(3, "当天签约");

        private Integer code;
        private String desc;

        DealState(Integer code, String desc) {
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
        wait(0, "等待支付"),
        closed(-1, "交易关闭"),
        refund(2, "交易退款"),
        payed(1, "交易完成");

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
        if (State.closed.getCode().equals(state)){
            return State.closed.getDesc();
        } else if (State.wait.getCode().equals(state)){
            return State.wait.getDesc();
        } else if (State.refund.getCode().equals(state)){
            return State.refund.getDesc();
        }else {
            return State.payed.getDesc();
        }
    }

    public String getDealStateDesc(){
        if (DealState.notice_fail.getCode().equals(dealState)){
            return DealState.notice_fail.getDesc();
        } else if (DealState.notice_wait.getCode().equals(dealState)){
            return DealState.notice_wait.getDesc();
        } else if (DealState.settle_wait.getCode().equals(dealState)){
            return DealState.settle_wait.getDesc();
        }else if (DealState.settle_fail.getCode().equals(dealState)){
            return DealState.settle_fail.getDesc();
        }else if (DealState.today_sign.getCode().equals(dealState)){
            return DealState.today_sign.getDesc();
        }else if (DealState.settled.getCode().equals(dealState)){
            return DealState.settled.getDesc();
        }else {
            return DealState.repayment_success.getDesc();
        }
    }


    public String getRedPacketStateDesc(){
        if (RedPackState.receive.getCode().equals(redPackState)){
            return RedPackState.receive.getDesc();
        } else {
            return RedPackState.un_receive.getDesc();
        }
    }

    public Boolean isRepaymentSuccess() {
        return DealState.repayment_success.getCode().equals(state);
    }

    public Boolean isTodaySign() {
        return DealState.today_sign.getCode().equals(state);
    }

    public Boolean isSettleWait() {
        return DealState.settle_wait.getCode().equals(state);
    }

    public Boolean isNoticeFail() {
        return DealState.notice_fail.getCode().equals(state);
    }
    public Boolean isSettled() {
        return DealState.settled.getCode().equals(state);
    }
    public Boolean isPayed() {
        return State.payed.getCode().equals(state);
    }


    public Boolean isRefund() {
        return State.refund.getCode().equals(state);
    }


    public Boolean isClosed() {
        return State.closed.getCode().equals(state);
    }

    public Boolean isWait() {
        return State.wait.getCode().equals(state);
    }
}

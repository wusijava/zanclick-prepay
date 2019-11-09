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

    private String reason;


    public enum DealState {
        notice_wait(4, "等待通知"),
        notice_fail(0, "通知失败"),
        settle_fail(1, "结算失败"),
        settle_wait(2, "等待结算"),
        today_sign(3, "当天签约的");

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
        closed(-1, "关闭的"),
        refund(2, "已退款"),
        payed(1, "已支付");

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

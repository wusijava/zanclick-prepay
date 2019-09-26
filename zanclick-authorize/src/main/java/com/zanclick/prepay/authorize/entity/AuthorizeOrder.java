package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 资金授权订单
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeOrder implements Identifiable<Long> {

    private Long id;

    private String merchantNo;

    private String storeNo;

    private String appId;

    private String title;

    private String timeout;

    private String qrCodeUrl;

    /**冻结金额**/
    private String money;

    /**交易方式（扫码，被扫）**/
    private Integer dealType;

    /**是否自动结算**/
    private Integer settleType;

    /**支持的支付方式**/
    private Integer payWay;

    /**
     * 授权码
     * */
    private String authNo;

    /**
     * 订单号（第三方提供）
     * */
    private String outTradeNo;
    /**
     * 支付宝提供
     * */
    private String orderNo;
    /**
     * 交易订单号
     * */
    private String requestNo;

    /**
     * 结算日期
     * */
    private String settleDate;

    private Date createTime;

    private Date finishTime;

    private String buyerId;

    private String buyerNo;

    private String operationId;

    /**
     * 收款支付宝账户，以订单产生的时候为准
     * 收款商户的实名名称（并非预授权收款方，为垫资方）
     * */
    private String sellerName;
    /**
     * 收款支付宝账户，以订单产生的时候为准
     * 收款商户的支付宝账号（并非预授权收款方，为垫资方）
     * */
    private String sellerNo;

    private String contactName;

    private String contactPhone;

    private String requestContent;

    private Long configurationId;

    /** 记录状态**/
    private Integer state;

    /***
     * 数据传递用
     * */
    private AuthorizeOrderFee fee;

    public Boolean isPayed(){
        return State.payed.getCode().equals(state);
    }

    public Boolean isSettled(){
        return  State.settled.getCode().equals(state) ;
    }

    public Boolean isSettling(){
        return State.settling.getCode().equals(state);
    }

    public Boolean isUnPay(){
        return State.unPay.getCode().equals(state) || State.paying.getCode().equals(state);
    }

    public Boolean isFail(){ return State.closed.getCode().equals(state) || State.failed.getCode().equals(state);}

    public Boolean isRefund(){
        return State.refund.getCode().equals(state);
    }

    public enum State{
        closed(-2,"交易关闭"),
        failed(-1,"交易失败"),
        unPay(0,"等待支付"),
        paying(2,"支付中"),
        payed(1,"支付成功"),
        settling(3,"结算中"),
        settled(4,"结算成功"),
        refund(5,"已解冻");

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

    public enum PayWay{
        PCREDIT(0,"花呗"),
        MONEY(1,"余额宝"),
        ALL(2,"全部");

        private Integer code;
        private String desc;

        PayWay(Integer code, String desc) {
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

    public enum DealType{
        SCAN(0,"扫码"),
        MICRO(1,"刷卡");

        private Integer code;
        private String desc;

        DealType(Integer code, String desc) {
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

    public enum SettleType{
        NO(0,"否"),
        YES(1,"是");

        private Integer code;
        private String desc;

        SettleType(Integer code, String desc) {
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

}

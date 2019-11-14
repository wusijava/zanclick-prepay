package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author lvlu
 * @date 2019-05-10 13:39
 **/
@Data
public class SupplyChainTrade implements Identifiable<Long> {

    private Long id;

    /**
     * 授权订单号
     */
    private String authNo;
    /**
     * 支付宝返回的操作号
     */
    private String opId;
    /**
     * 冻结的用户支付宝UID
     */
    private String freezeUserId;

    /**
     * 外部订单号
     * */
    private String outTradeNo;

    /**
     * 冻结用户资金操作时，产生并传过去的 out_request_no
     */
    private String outRequestNo;

    /**
     * 总授权金额
     */
    private String totalAmount;

    /**
     * 还款分期数
     */
    private Integer fqNum;

    private Date freezeDate;

    private Date expireDate;

    private String title;

    /**
     * 收款人支付宝登录账号
     */
    private String rcvLoginId;

    /**
     * 收款支付宝账号实名名称
     */
    private String rcvAlipayName;

    /**
     * 收款人联系名称
     */
    private String rcvContactName;

    /**
     * 收款人联系电话
     */
    private String rcvContactPhone;

    /**
     * 收款人联系邮件
     */
    private String rcvContactEmail;

    /**
     * 交易创建放款金额
     */
    private String amount;

    /**
     * 网商手续费
     */
    private String mybankFee;

    /**
     * 点赞手续费
     */
    private String clickFee;

    private String requestId;

    private String tradeNo;

    private String factoringNo;

    private Long factoringAmt;

    private String currency;

    private Date createTime;

    /**
     * 放款时间
     */
    private Date loanTime;

    /**
     * 下一期截止还款时间
     */
    private Date nextPayTime;

    /**
     * 结清时间,或者取消时间
     */
    private Date finishTime;

    private String clearDate;

    /**
     * 当前状态
     */
    private Integer state;

    private Long configurationId;

    /**
     * 创建垫资订单失败原因
     */
    private String failReason;

    /**
     * 采购内容
     */
    private String purchaseContent;

    /**
     * 账款备注
     */
    private String receivableRemark;


    public boolean isFail(){
        return TradeStateEnum.FAILED.getCode().equals(state) || TradeStateEnum.CANCELED.getCode().equals(state);
    }


}

package com.zanclick.prepay.authorize.entity;

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
public class AuthorizeMerchant implements Identifiable<Long> {

    private Long id;

    private String merchantNo;

    private String appId;

    /**
     * 商户收款支付宝账号
     */
    private String sellerNo;

    /**
     * 商户收款支付宝ID
     **/
    private String sellerId;

    /**
     * 支付宝实名名称
     **/
    private String name;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    private String operatorName;

    private String storeNo;

    private String storeName;

    private String storeSubjectName;

    private String storeSubjectCertNo;

    /**
     * 省
     */
    private String storeProvince;

    /**
     * 市
     */
    private String storeCity;

    /**
     * 区
     */
    private String storeCounty;

    /**
     * 支付宝返回的供应商编号
     */
    private String supplierNo;

    private String reason;

    private Date createTime;

    private Date finishTime;

    /**
     * 记录状态
     **/
    private Integer state;

    public enum State {
        failed(-1, "创建失败"),
        waiting(0, "等待提交"),
        success(1, "签约成功");

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

    public Boolean isSuccess() {
        return state.equals(State.success.getCode());
    }

    public Boolean isFail() {
        return state.equals(State.failed.getCode());
    }
}

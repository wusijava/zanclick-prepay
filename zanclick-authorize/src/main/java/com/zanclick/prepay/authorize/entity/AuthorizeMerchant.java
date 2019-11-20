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

    /**
     * 渠道标识
     */
    private String wayId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 应用ID
     */
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

    /**
     * 运营商名称
     */
    private String operatorName;

    /**
     * 门店编号
     */
    private String storeNo;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 营业执照名称
     */
    private String storeSubjectName;

    /**
     * 营业执照编号
     */
    private String storeSubjectCertNo;

    /**
     * 省
     */
    private String storeProvince;

    private String storeProvinceCode;
    /**
     * 市
     */
    private String storeCity;

    private String storeCityCode;
    /**
     * 区
     */
    private String storeCounty;

    private String storeCountyCode;

    /**
     * 支付宝返回的供应商编号
     */
    private String supplierNo;

    /**
     * 记录状态
     **/
    private Integer state;

    private Date createTime;

    private Date finishTime;

    private Integer redPackState;

    private String redPackSellerNo;

    private String reason;

    private String storeMarkCode;

    /**
     * 是否开启自由领取红包
     * */
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

    public String getStateDesc() {
        if (AuthorizeMerchant.State.success.getCode().equals(state)) {
            return "签约成功";
        } else if (AuthorizeMerchant.State.failed.getCode().equals(state)) {
            return "签约失败";
        } else {
            return "等待签约";
        }
    }

    public Boolean isSuccess() {
        return state.equals(State.success.getCode());
    }

    public Boolean isFail() {
        return state.equals(State.failed.getCode());
    }
}

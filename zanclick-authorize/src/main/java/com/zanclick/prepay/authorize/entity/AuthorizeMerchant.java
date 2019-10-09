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

    private String reason;

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

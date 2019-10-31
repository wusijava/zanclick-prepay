package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 预授权配置信息
 *
 * @author duchong
 * @date 2019-7-4 11:20:31
 */
@Data
public class AuthorizeConfiguration implements Identifiable<Long> {
    private Long id;

    /**
     * 账号名称
     * */
    private String name;

    /**
     * 应用appid
     */
    private String isvAppId;
    /**
     * isv用户id
     */
    private String isvUid;
    /**
     * 签名类型
     */
    private String signType;

    /**
     * 支付宝网关
     */
    private String gateway;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 分账uid
     */
    private String royaltyUid;

    /**
     * isv联系电话
     */
    private String isvNumber;

    /**
     * 分佣
     */
    private String feeBackUid;

    /**
     * 服务商ID
     * */
    private String serviceProviderId;

    /**
     * 网商垫资专用
     * */
    private String ipId;

    /**
     * 网商垫资专用
     * */
    private String roleId;

    private String myBankAppId;

    private String myBankPublicKey;

    private String myBankPrivateKey;

    private String myBankUid;

    private String myBankSellerNo;

    /**
     * 支付宝账号
     */
    private String sellerNo;

    private String format;

    private String charset;

    /**
     * 当前状态0关闭1启用
     */
    private Integer status;

    /**
     * 是否默认收款|提现(1默认)
     */
    private Integer isDefault;


}


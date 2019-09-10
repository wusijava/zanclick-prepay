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

    private String name;

    /**
     * 应用appid
     */
    private String isv_appid;
    /**
     * isv用户id
     */
    private String isv_uid;
    /**
     * 签名类型
     */
    private String sign_type;

    /**
     * 支付宝网关
     */
    private String gateway;
    /**
     * 私钥
     */
    private String private_key;
    /**
     * 公钥
     */
    private String public_key;

    /**
     * 分账uid
     */
    private String royalty_uid;

    /**
     * isv联系电话
     */
    private String isv_number;

    /**
     * 分佣
     */
    private String fee_backuid;

    /**
     * 当前状态0关闭1启用
     */
    private Integer status;

    /**
     * 是否默认收款|提现(1默认)
     */
    private Integer is_default;

    /**
     * 支付宝账号
     */
    private String seller_no;

    private Long appid;
}


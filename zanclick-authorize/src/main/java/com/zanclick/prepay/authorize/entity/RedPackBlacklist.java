package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/11/22 11:16
 * @Description 可领红包黑名单
 **/
@Data
public class RedPackBlacklist implements Identifiable<Long> {

    /**
     * id
     */
    private Long id;

    /**
     * 支付宝账号
     */
    private String sellerNo;

    /**
     * 账号名称
     **/
    private String name;

    /**
     * 创建时间
     */
    private String createTime;
}

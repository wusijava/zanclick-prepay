package com.zanclick.prepay.authorize.vo.web;

import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/11/22 16:11
 * @Description //
 **/
@Data
public class BlacklistWebInfo {

    private Long id;

    /**
     * 商户收款支付宝账号
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

    /**
     * 结款类型
     */
    private String type;

}

package com.zanclick.prepay.authorize.vo.web;

import lombok.Data;

import java.util.Date;

/**
 * @Author panliang
 * @Date 2019/12/2 10:39
 * @Description //
 **/
@Data
public class RedPacketConfigurationWebInfo {
    /**
     * id
     */
    private Long id;

    /**
     * 名称
     **/
    private String name;

    /**
     * 级别(1.门店 2.市 3.省)
     **/
    private String level;

    /**
     * 状态:1启用/0关闭
     */
    private String status;

    /**
     * 创建时间
     */
    private String createTime;
}

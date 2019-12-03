package com.zanclick.prepay.authorize.entity;

import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/12/3 16:50
 * @Description //
 **/
@Data
public class RedPacketConfigurationRecord extends RedPacketConfiguration{
    /**
     * 修改人id
     */
    private String userId;
    /**
     * 修改人ip地址
     */
    private String address;
}

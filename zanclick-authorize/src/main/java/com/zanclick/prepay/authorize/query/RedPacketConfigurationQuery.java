package com.zanclick.prepay.authorize.query;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/12/2 10:37
 * @Description //
 **/
@Data
public class RedPacketConfigurationQuery extends RedPacketConfiguration {

    private Integer page;

    private Integer limit;

    private Integer offset;

}

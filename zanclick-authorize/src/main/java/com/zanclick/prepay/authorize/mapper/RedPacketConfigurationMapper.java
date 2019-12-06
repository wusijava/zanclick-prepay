package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;

import java.util.Map;

public interface RedPacketConfigurationMapper extends BaseMapper<RedPacketConfiguration, Long> {

    /**
     * 根据级别和代码查询
     *
     * @param params
     */
    RedPacketConfiguration selectByLevelAndNameCode(Map<String,Object> params);
}

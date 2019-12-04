package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.authorize.mapper.RedPacketConfigurationMapper;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationService;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author panliang
 * @Date 2019/12/2 10:47
 * @Description //
 **/
@Slf4j
@Service
public class RedPacketConfigurationServiceImpl extends BaseMybatisServiceImpl<RedPacketConfiguration, Long> implements RedPacketConfigurationService {

    @Autowired
    private RedPacketConfigurationMapper redPackBlacklistMapper;

    @Override
    protected BaseMapper<RedPacketConfiguration, Long> getBaseMapper() {
        return redPackBlacklistMapper;
    }

}

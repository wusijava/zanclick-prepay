package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.authorize.entity.RedPacketConfigurationRecord;
import com.zanclick.prepay.authorize.mapper.RedPacketConfigurationRecordMapper;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationRecordService;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author panliang
 * @Date 2019/12/3 17:03
 * @Description //
 **/
@Slf4j
@Service
public class RedPacketConfigurationRecordServiceImpl extends BaseMybatisServiceImpl<RedPacketConfigurationRecord, Long> implements RedPacketConfigurationRecordService {

    @Autowired
    private RedPacketConfigurationRecordMapper redPacketConfigurationRecordMapper;

    @Override
    protected BaseMapper<RedPacketConfigurationRecord, Long> getBaseMapper() {
        return redPacketConfigurationRecordMapper;
    }
}

package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.SettleRate;
import com.zanclick.prepay.order.mapper.SettleRateMapper;
import com.zanclick.prepay.order.service.SettleRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-10-31 14:21:08
 **/
@Service
public class SettleRateServiceImpl extends BaseMybatisServiceImpl<SettleRate,Long> implements SettleRateService {

    @Autowired
    private SettleRateMapper settleRateMapper;


    @Override
    protected BaseMapper<SettleRate, Long> getBaseMapper() {
        return settleRateMapper;
    }

    @Override
    public SettleRate queryByAppId(String appId) {
        return settleRateMapper.selectByAppId(appId);
    }
}

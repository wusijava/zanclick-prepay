package com.zanclick.prepay.app.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.mapper.AppInfoMapper;
import com.zanclick.prepay.app.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-09-10 14:47:38
 **/
@Service
public class AppInfoServiceImpl extends BaseMybatisServiceImpl<AppInfo,Long> implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    protected BaseMapper<AppInfo, Long> getBaseMapper() {
        return appInfoMapper;
    }

    @Override
    public AppInfo queryByAppId(String appId) {
        return appInfoMapper.selectByAppId(appId);
    }
}

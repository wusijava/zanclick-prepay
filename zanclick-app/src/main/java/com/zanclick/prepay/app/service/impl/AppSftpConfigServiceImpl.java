package com.zanclick.prepay.app.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.app.entity.AppSftpConfig;
import com.zanclick.prepay.app.mapper.AppSftpConfigMapper;
import com.zanclick.prepay.app.service.AppSftpConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zanclick
 * @date 2019-10-14 15:44:15
 **/
@Service
public class AppSftpConfigServiceImpl extends BaseMybatisServiceImpl<AppSftpConfig,Long> implements AppSftpConfigService {

    @Autowired
    private AppSftpConfigMapper appSftpConfigMapper;


    @Override
    protected BaseMapper<AppSftpConfig, Long> getBaseMapper() {
        return appSftpConfigMapper;
    }
}

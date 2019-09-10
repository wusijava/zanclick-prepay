package com.zanclick.prepay.app.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.app.entity.AppInfo;

/**
 * @author Administrator
 * @date 2019-09-10 14:47:38
 **/
public interface AppInfoService extends BaseService<AppInfo,Long> {

    /**
     * 根据appId查询
     *
     * @param appId
     * @return
     */
    AppInfo queryByAppId(String appId);

}

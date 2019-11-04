package com.zanclick.prepay.web.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.web.entity.MethodSwitch;

/**
 * @author Administrator
 * @date 2019-11-01 16:13:14
 **/
public interface MethodSwitchService extends BaseService<MethodSwitch,Long> {

    /**
     * 根据appId和方法名称查询
     *
     * @param method
     * @param appId
     * @return
     */
    MethodSwitch queryByMethodAndAppId(String method,String appId);
}

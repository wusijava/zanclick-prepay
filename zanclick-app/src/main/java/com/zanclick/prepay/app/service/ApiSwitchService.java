package com.zanclick.prepay.app.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.app.entity.ApiSwitch;

import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-21 18:37:04
 **/
public interface ApiSwitchService extends BaseService<ApiSwitch,Long> {


    /**
     * 根据路径查询
     *
     * @param path
     * @return
     */
    ApiSwitch selectByPath(String path);


    /**
     * 根据路径查询
     *
     * @param path
     * @param type
     * @return
     */
    ApiSwitch selectByMap(String path,Integer type);
}

package com.zanclick.prepay.setmeal.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.setmeal.entity.MethodSwitch;
import com.zanclick.prepay.setmeal.mapper.MethodSwitchMapper;
import com.zanclick.prepay.setmeal.service.MethodSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-01 16:13:14
 **/
@Service
public class MethodSwitchServiceImpl extends BaseMybatisServiceImpl<MethodSwitch, Long> implements MethodSwitchService {

    @Autowired
    private MethodSwitchMapper methodSwitchMapper;

    @Override
    protected BaseMapper<MethodSwitch, Long> getBaseMapper() {
        return methodSwitchMapper;
    }

    static Map<String, Map<String, MethodSwitch>> appMap = new HashMap<>();


    @Override
    public MethodSwitch queryByMethodAndAppId(String method, String appId) {
      return getAppMethod(method, appId);
    }




    private MethodSwitch getMethod(String method, String appId) {
        String key = "api_method";
        Object object = RedisUtil.get(key);
        if (object == null){
            appMap = new HashMap<>();
            RedisUtil.set(key,System.currentTimeMillis(),60*60*24);
        }
        Map<String, MethodSwitch> switchMap = appMap.get(appId);
        if (switchMap == null){
            switchMap = new HashMap<>();
            appMap.put(appId,switchMap);
        }
        MethodSwitch methodSwitch = switchMap.get(method);
        if (methodSwitch == null){
            methodSwitch = getAppMethod(method, appId);
            if (methodSwitch != null){
                switchMap.put(methodSwitch.getMethod(),methodSwitch);
                appMap.put(appId,switchMap);
            }
        }
        return methodSwitch;
    }

    /**
     * 获取方法相关
     *
     * @param appId
     */
    private MethodSwitch getAppMethod(String method,String appId) {
        Map<String, Object> params = new HashMap<>();
        params.put("method", method);
        params.put("appId", appId);
        return methodSwitchMapper.selectByMethodAndAppId(params);
    }
}

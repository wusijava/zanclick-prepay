package com.zanclick.prepay.setmeal.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
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
public class MethodSwitchServiceImpl extends BaseMybatisServiceImpl<MethodSwitch,Long> implements MethodSwitchService {

    @Autowired
    private MethodSwitchMapper methodSwitchMapper;


    @Override
    protected BaseMapper<MethodSwitch, Long> getBaseMapper() {
        return methodSwitchMapper;
    }

    @Override
    public MethodSwitch queryByMethodAndAppId(String method, String appId) {
        Map<String,Object> params = new HashMap<>();
        params.put("method",method);
        params.put("appId",appId);
        return methodSwitchMapper.selectByMethodAndAppId(params);
    }
}

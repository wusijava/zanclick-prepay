package com.zanclick.prepay.app.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.app.entity.ApiSwitch;
import com.zanclick.prepay.app.mapper.ApiSwitchMapper;
import com.zanclick.prepay.app.service.ApiSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-21 18:37:04
 **/
@Service
public class ApiSwitchServiceImpl extends BaseMybatisServiceImpl<ApiSwitch,Long> implements ApiSwitchService {

    @Autowired
    private ApiSwitchMapper apiSwitchMapper;


    @Override
    protected BaseMapper<ApiSwitch, Long> getBaseMapper() {
        return apiSwitchMapper;
    }

    @Override
    public ApiSwitch selectByPath(String path) {
        return apiSwitchMapper.selectByPath(path);
    }

    @Override
    public ApiSwitch selectByMap(String path, Integer type) {
        Map<String,Object> params = new HashMap<>();
        params.put("path",path);
        params.put("type",type);
        return apiSwitchMapper.selectByMap(params);
    }
}

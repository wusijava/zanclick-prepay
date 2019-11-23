package com.zanclick.prepay.app.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.app.entity.ApiSwitch;
import com.zanclick.prepay.app.mapper.ApiSwitchMapper;
import com.zanclick.prepay.app.service.ApiSwitchService;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-21 18:37:04
 **/
@Service
public class ApiSwitchServiceImpl extends BaseMybatisServiceImpl<ApiSwitch, Long> implements ApiSwitchService {

    @Autowired
    private ApiSwitchMapper apiSwitchMapper;

    @Override
    protected BaseMapper<ApiSwitch, Long> getBaseMapper() {
        return apiSwitchMapper;
    }

    @Override
    public ApiSwitch queryByPath(String path) {
        return apiSwitchMapper.selectByPath(path);
    }

    @Override
    public ApiSwitch queryByPathAndType(String path, Integer type) {
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("type", type);
        return apiSwitchMapper.selectByMap(params);
    }

    @Override
    public Boolean hasPermission(String path, Integer type) {
        Object object = RedisUtil.get("api_switch_list");
        List<ApiSwitch> switchList = object == null ? null : (List<ApiSwitch>) object;
        if (DataUtil.isEmpty(switchList)){
            switchList = this.queryList(new ApiSwitch());
            if (DataUtil.isEmpty(switchList)){
                return true;
            }
            RedisUtil.set("api_switch_list",switchList,60*60);
        }
        for (ApiSwitch apiSwitch:switchList){
            if (apiSwitch.getPath().equals(path) && apiSwitch.getType().equals(type)){
                return apiSwitch.isOpen();
            }
        }
        return true;
    }
}

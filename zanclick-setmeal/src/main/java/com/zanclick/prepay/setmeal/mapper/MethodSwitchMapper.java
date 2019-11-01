package com.zanclick.prepay.setmeal.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.setmeal.entity.MethodSwitch;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-01 16:13:14
 **/
@Mapper
public interface MethodSwitchMapper extends BaseMapper<MethodSwitch, Long> {

    /**
     * 根据appId和方法名称查询
     *
     * @param params
     * @return
     */
    MethodSwitch selectByMethodAndAppId(Map<String, Object> params);
}

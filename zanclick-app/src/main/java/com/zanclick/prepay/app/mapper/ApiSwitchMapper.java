package com.zanclick.prepay.app.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.app.entity.ApiSwitch;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Administrator
 * @date 2019-11-21 18:37:04
 **/
@Mapper
public interface ApiSwitchMapper extends BaseMapper<ApiSwitch,Long> {

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
     * @param params
     * @return
     */
    ApiSwitch selectByMap(Map<String,Object> params);
}

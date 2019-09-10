package com.zanclick.prepay.app.mapper;

import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-09-10 14:47:38
 **/
@Mapper
public interface AppInfoMapper extends BaseMapper<AppInfo, Long> {

    /**
     * 根据appId查询
     *
     * @param appId
     * @return
     */
    AppInfo selectByAppId(String appId);
}

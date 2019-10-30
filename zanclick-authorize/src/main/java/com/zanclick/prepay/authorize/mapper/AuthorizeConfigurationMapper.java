package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;

/**
 * 预授权的支付宝通道
 *
 * @author duchong
 * @date 2019-3-6 10:51:58
 **/
public interface AuthorizeConfigurationMapper extends BaseMapper<AuthorizeConfiguration, Long> {

    /**
     * 获取默认的直付通通道
     *
     * @return
     */
    AuthorizeConfiguration selectDefaultConfiguration();


    /**
     * 根据appId查询
     *
     * @param isvAppId
     * @return
     */
    AuthorizeConfiguration selectByIsvAppId(String isvAppId);


}

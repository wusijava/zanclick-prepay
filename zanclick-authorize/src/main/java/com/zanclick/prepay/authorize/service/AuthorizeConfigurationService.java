package com.zanclick.prepay.authorize.service;

import com.alipay.api.AlipayClient;
import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;

/**
 * 预授权的支付宝通道
 *
 * @author duchong
 * @date 2019-3-6 10:51:58
 **/
public interface AuthorizeConfigurationService extends BaseService<AuthorizeConfiguration, Long> {
    /**
     * 获取默认的直付通配置
     *
     * @return
     */
    AuthorizeConfiguration queryDefaultConfiguration();

    /**
     * 获取默认的支付宝配置
     *
     * @return
     */
    AlipayClient queryDefaultAlipayClient();

    /**
     * 通过直付通获取支付宝配置
     *
     * @param configuration
     * @return
     */
    AlipayClient getAlipayClient(AuthorizeConfiguration configuration);

    /**
     * 通过ID获取支付宝配置
     *
     * @param id
     * @return
     */
    AlipayClient queryAlipayClientById(Long id);
}

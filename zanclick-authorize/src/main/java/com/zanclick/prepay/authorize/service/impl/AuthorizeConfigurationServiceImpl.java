package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.mapper.AuthorizeConfigurationMapper;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 预授权的支付宝通道
 *
 * @author duchong
 * @date 2019-3-6 10:51:27
 **/
@Slf4j
@Service
public class AuthorizeConfigurationServiceImpl extends BaseMybatisServiceImpl<AuthorizeConfiguration, Long> implements AuthorizeConfigurationService {

    @Autowired
    private AuthorizeConfigurationMapper zftConfigurationMapper;

    @Override
    protected BaseMapper<AuthorizeConfiguration, Long> getBaseMapper() {
        return zftConfigurationMapper;
    }

    private static Map<Long, AuthorizeConfiguration> configurationMap = new HashMap<>();

    private static AuthorizeConfiguration defaultConfiguration = null;

    @Override
    public AuthorizeConfiguration queryDefaultConfiguration() {
        AuthorizeConfiguration configuration = defaultConfiguration;
        if (configuration == null) {
            configuration = zftConfigurationMapper.selectDefaultConfiguration();
            if (configuration == null) {
                log.error("缺少默认支付配置");
                return null;
            }
            configurationMap.put(configuration.getId(), configuration);
            defaultConfiguration = configuration;
        }
        return configuration;
    }

    @Override
    public AlipayClient queryDefaultAlipayClient() {
        if (defaultConfiguration == null) {
            defaultConfiguration = queryDefaultConfiguration();
        }
        return getAliPayClient(defaultConfiguration);
    }


    @Override
    public AlipayClient getAlipayClient(AuthorizeConfiguration configuration) {
        return getAliPayClient(configuration);
    }

    @Override
    public AlipayClient queryAlipayClientById(Long id) {
        AuthorizeConfiguration configuration = configurationMap.get(id);
        if (configuration == null) {
            configuration = this.queryById(id);
            if (configuration == null) {
                log.error("支付配置信息异常:{}", id);
                return null;
            }
            configurationMap.put(id, configuration);
        }
        return getAliPayClient(configuration);
    }

    private AlipayClient getAliPayClient(AuthorizeConfiguration configuration) {
        return new DefaultAlipayClient(configuration.getGateway(), configuration.getIsv_appid(), configuration.getPrivate_key(), "json", "utf-8", configuration.getPublic_key(), configuration.getSign_type());
    }
}

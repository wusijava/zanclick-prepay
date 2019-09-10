package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeFee;
import com.zanclick.prepay.authorize.mapper.AuthorizeFeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 网商预授权垫资费率
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service
public class AuthorizeFeeServiceImpl extends BaseMybatisServiceImpl<AuthorizeFee, Long> implements AuthorizeFeeService {

    @Autowired
    private AuthorizeFeeMapper authorizeFeeMapper;

    @Override
    protected BaseMapper<AuthorizeFee, Long> getBaseMapper() {
        return authorizeFeeMapper;
    }

    @Override
    public String queryByAppId(String appId) {
        AuthorizeFee fee = authorizeFeeMapper.selectByAppId(appId);
        if (fee == null){
            log.error("费率未配置:{}",appId);
            return "0.00";
        }
        return fee.getFee();
    }
}

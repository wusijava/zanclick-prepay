package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderFee;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderFeeMapper;
import com.zanclick.prepay.authorize.service.AuthorizeOrderFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权订单
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public class AuthorizeOrderFeeServiceImpl extends BaseMybatisServiceImpl<AuthorizeOrderFee, Long> implements AuthorizeOrderFeeService {

    @Autowired
    private AuthorizeOrderFeeMapper authorizeOrderFeeMapper;

    @Override
    protected BaseMapper<AuthorizeOrderFee, Long> getBaseMapper() {
        return authorizeOrderFeeMapper;
    }


    @Override
    public AuthorizeOrderFee queryByRequestNo(String requestNo) {
        return authorizeOrderFeeMapper.selectByRequestNo(requestNo);
    }

    @Override
    public AuthorizeOrderFee queryByOrderNo(String orderNo) {
        return authorizeOrderFeeMapper.selectByOrderNo(orderNo);
    }
}

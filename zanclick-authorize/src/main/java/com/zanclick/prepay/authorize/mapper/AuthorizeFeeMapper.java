package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeFee;

/**
 * 网商预授权垫资费率
 *
 * @author duchong
 * @date 2019-7-8 18:23:41
 */
public interface AuthorizeFeeMapper extends BaseMapper<AuthorizeFee, Long> {

    /**
     * 根据appId查询费率
     *
     * @param appId
     * @return
     */
    AuthorizeFee selectByAppId(String appId);
}

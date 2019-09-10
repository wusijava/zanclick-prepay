package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeFee;

/**
 * 网商预授权垫资费率
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeFeeService extends BaseService<AuthorizeFee, Long> {


    /**
     * 根据appId查询费率
     *
     * @param appId
     * @return
     */
    String queryByAppId(String appId);


}

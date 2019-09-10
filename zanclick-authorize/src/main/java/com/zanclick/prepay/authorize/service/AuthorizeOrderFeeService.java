package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderFee;

/**
 * 预授权订单费用详情
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeOrderFeeService extends BaseService<AuthorizeOrderFee, Long> {


    /**
     * 根据订单号查询订单
     *
     * @param requestNo
     * @return
     */
    AuthorizeOrderFee queryByRequestNo(String requestNo);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    AuthorizeOrderFee queryByOrderNo(String orderNo);

}

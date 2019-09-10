package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderFee;

/**
 * 直付通预授权订单费用明细
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeOrderFeeMapper extends BaseMapper<AuthorizeOrderFee, Long> {


    /**
     * 根据订单号查询订单
     *
     * @param requestNo
     * @return
     */
    AuthorizeOrderFee selectByRequestNo(String requestNo);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    AuthorizeOrderFee selectByOrderNo(String orderNo);

}

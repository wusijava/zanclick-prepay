package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeRefundOrder;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeRefundOrderMapper extends BaseMapper<AuthorizeRefundOrder, Long> {

    /**
     * 根据退款订单号查询退款订单
     *
     * @param refundNo
     * @return
     */
    AuthorizeRefundOrder selectByRequestNo(String refundNo);

    /**
     * 根据退款订单号查询退款订单
     *
     * @param refundNo
     * @return
     */
    AuthorizeRefundOrder selectByOutRequestNo(String refundNo);

}

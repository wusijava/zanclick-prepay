package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRefundRecord;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeOrderRefundRecordMapper extends BaseMapper<AuthorizeOrderRefundRecord, Long> {

    /**
     * 根据退款订单号查询退款订单
     *
     * @param refundNo
     * @return
     */
    AuthorizeOrderRefundRecord selectByRefundNo(String refundNo);


    /**
     * 根据退款订单号查询退款订单
     *
     * @param outRefundNo
     * @return
     */
    AuthorizeOrderRefundRecord selectByOutRefundNo(String outRefundNo);


    /**
     * 根据退款订单号查询退款订单
     *
     * @param rquestNo
     * @return
     */
    AuthorizeOrderRefundRecord selectByRequestNo(String rquestNo);

}

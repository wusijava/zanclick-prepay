package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeRefundOrder;
import com.zanclick.prepay.authorize.mapper.AuthorizeRefundOrderMapper;
import com.zanclick.prepay.authorize.service.AuthorizeRefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 预授权退款订单
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public class AuthorizeRefundOrderServiceImpl extends BaseMybatisServiceImpl<AuthorizeRefundOrder, Long> implements AuthorizeRefundOrderService {

    @Autowired
    private AuthorizeRefundOrderMapper authorizeRefundOrderMapper;

    @Override
    protected BaseMapper<AuthorizeRefundOrder, Long> getBaseMapper() {
        return authorizeRefundOrderMapper;
    }


    @Override
    public AuthorizeRefundOrder queryByRefundNo(String refundNo) {
        return authorizeRefundOrderMapper.selectByRefundNo(refundNo);
    }

    @Override
    public AuthorizeRefundOrder createRefundOrder(String amount,String orderNo,String requestNo,String refundNo,String reason) {
        AuthorizeRefundOrder refund = new AuthorizeRefundOrder();
        refund.setCreateTime(new Date());
        refund.setAmount(amount);
        refund.setOrderNo(orderNo);
        refund.setRefundReason(reason);
        refund.setRefundNo(refundNo);
        refund.setRequestNo(requestNo);
        refund.setState(0);
        getBaseMapper().insert(refund);
        return refund;
    }

    @Override
    public void refundFail(AuthorizeRefundOrder refund) {
        refund.setFinishTime(new Date());
        refund.setState(-1);
        this.updateById(refund);
    }

    @Override
    public void refundSuccess(AuthorizeRefundOrder refund) {
        refund.setFinishTime(new Date());
        refund.setState(1);
        this.updateById(refund);
    }
}

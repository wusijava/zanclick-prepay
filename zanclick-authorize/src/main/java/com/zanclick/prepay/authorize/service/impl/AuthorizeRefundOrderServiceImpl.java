package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeRefundOrder;
import com.zanclick.prepay.authorize.mapper.AuthorizeRefundOrderMapper;
import com.zanclick.prepay.authorize.service.AuthorizeRefundOrderService;
import com.zanclick.prepay.common.utils.StringUtils;
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
    public AuthorizeRefundOrder queryByRequestNo(String requestNo) {
        return authorizeRefundOrderMapper.selectByRequestNo(requestNo);
    }

    @Override
    public AuthorizeRefundOrder queryByOutRequestNo(String outRequestNo) {
        return authorizeRefundOrderMapper.selectByOutRequestNo(outRequestNo);
    }

    @Override
    public AuthorizeRefundOrder createRefundOrder(String amount,String orderNo,String outRequestNo,String authNo,Integer type,String reason) {
        AuthorizeRefundOrder refund = new AuthorizeRefundOrder();
        refund.setCreateTime(new Date());
        refund.setAmount(amount);
        refund.setOrderNo(orderNo);
        refund.setRefundReason(reason);
        refund.setOutRequestNo(outRequestNo);
        refund.setRequestNo(StringUtils.getTradeNo());
        refund.setState(AuthorizeRefundOrder.State.wait.getCode());
        refund.setType(type);
        getBaseMapper().insert(refund);
        return refund;
    }

    @Override
    public void refundFail(AuthorizeRefundOrder refund) {
        refund.setFinishTime(new Date());
        refund.setState(AuthorizeRefundOrder.State.fail.getCode());
        this.updateById(refund);
    }

    @Override
    public void refundSuccess(AuthorizeRefundOrder refund) {
        refund.setFinishTime(new Date());
        refund.setState(AuthorizeRefundOrder.State.success.getCode());
        this.updateById(refund);
    }

    @Override
    public void refund(AuthorizeRefundOrder refund) {
        refund.setState(AuthorizeRefundOrder.State.refund.getCode());
        this.updateById(refund);
    }
}

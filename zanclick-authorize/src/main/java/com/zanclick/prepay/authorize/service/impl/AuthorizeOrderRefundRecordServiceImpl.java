package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRefundRecord;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderRefundRecordMapper;
import com.zanclick.prepay.authorize.service.AuthorizeOrderRefundRecordService;
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
public class AuthorizeOrderRefundRecordServiceImpl extends BaseMybatisServiceImpl<AuthorizeOrderRefundRecord, Long> implements AuthorizeOrderRefundRecordService {

    @Autowired
    private AuthorizeOrderRefundRecordMapper authorizeOrderRefundRecordMapper;

    @Override
    protected BaseMapper<AuthorizeOrderRefundRecord, Long> getBaseMapper() {
        return authorizeOrderRefundRecordMapper;
    }


    @Override
    public AuthorizeOrderRefundRecord queryByRefundNo(String refundNo) {
        return authorizeOrderRefundRecordMapper.selectByRefundNo(refundNo);
    }

    @Override
    public AuthorizeOrderRefundRecord createRefundOrder(String amount, String tradeNo, String refundNo, String reason) {
        AuthorizeOrderRefundRecord refund = new AuthorizeOrderRefundRecord();
        refund.setCreateTime(new Date());
        refund.setAmount(amount);
        refund.setTradeNo(tradeNo);
        refund.setRefundReason(reason);
        refund.setRefundNo(refundNo);
        refund.setState(0);
        getBaseMapper().insert(refund);
        return refund;
    }

    @Override
    public void refundFail(AuthorizeOrderRefundRecord refund) {
        refund.setFinishTime(new Date());
        refund.setState(-1);
        this.updateById(refund);
    }

    @Override
    public void refundSuccess(AuthorizeOrderRefundRecord refund) {
        refund.setFinishTime(new Date());
        refund.setState(1);
        this.updateById(refund);
    }
}

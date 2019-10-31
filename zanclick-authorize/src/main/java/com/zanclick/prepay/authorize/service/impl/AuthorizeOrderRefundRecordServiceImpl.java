package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRefundRecord;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderRefundRecordMapper;
import com.zanclick.prepay.authorize.service.AuthorizeOrderRefundRecordService;
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
    public AuthorizeOrderRefundRecord queryByOutRefundNo(String outRefundNo) {
        return authorizeOrderRefundRecordMapper.selectByOutRefundNo(outRefundNo);
    }

    @Override
    public AuthorizeOrderRefundRecord queryByRequestNo(String requestNo) {
        return authorizeOrderRefundRecordMapper.selectByRequestNo(requestNo);
    }

    @Override
    public AuthorizeOrderRefundRecord createRefundOrder(String amount, String requestNo, String outRefundNo, String reason) {
        AuthorizeOrderRefundRecord refund = new AuthorizeOrderRefundRecord();
        refund.setCreateTime(new Date());
        refund.setAmount(amount);
        refund.setRequestNo(requestNo);
        refund.setRefundReason(reason);
        refund.setOutRefundNo(outRefundNo);
        refund.setRefundNo(StringUtils.getTradeNo());
        refund.setState(AuthorizeOrderRefundRecord.State.wait.getCode());
        getBaseMapper().insert(refund);
        return refund;
    }

    @Override
    public void refundFail(AuthorizeOrderRefundRecord refund) {
        refund.setFinishTime(new Date());
        refund.setState(AuthorizeOrderRefundRecord.State.fail.getCode());
        this.updateById(refund);
    }

    @Override
    public void refundSuccess(AuthorizeOrderRefundRecord refund) {
        refund.setFinishTime(new Date());
        refund.setState(AuthorizeOrderRefundRecord.State.success.getCode());
        this.updateById(refund);
    }
}

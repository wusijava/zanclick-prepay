package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.authorize.service.MyBankSupplyChainService;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import com.zanclick.prepay.order.mapper.PayRefundOrderMapper;
import com.zanclick.prepay.order.query.PayRefundOrderQuery;
import com.zanclick.prepay.order.service.PayRefundOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-14 11:30:42
 **/
@Slf4j
@Service
public class PayRefundOrderServiceImpl extends BaseMybatisServiceImpl<PayRefundOrder, Long> implements PayRefundOrderService {

    @Autowired
    private PayRefundOrderMapper payRefundOrderMapper;
    @Autowired
    private MyBankSupplyChainService myBankSupplyChainService;

    @Override
    protected BaseMapper<PayRefundOrder, Long> getBaseMapper() {
        return payRefundOrderMapper;
    }

    @Override
    public PayRefundOrder queryByOutTradeNo(String outTradeNo) {
        PayRefundOrderQuery query = new PayRefundOrderQuery();
        query.setOutTradeNo(outTradeNo);
        List<PayRefundOrder> orderList = this.queryList(query);
        return orderList == null || orderList.size() == 0 ? null : orderList.get(0);
    }

    @Override
    public void settle(String outTradeNo) {
        PayRefundOrder refundOrder = this.queryByOutTradeNo(outTradeNo);
        if (refundOrder == null || refundOrder.getAuthNo() == null){
            log.error("订单数据异常，无法结清,{}",outTradeNo);
            throw new BizException("订单数据异常");
        }
        if (PayRefundOrder.RepaymentState.no_paid.getCode().equals(refundOrder.getRepaymentState())
                || PayRefundOrder.RepaymentState.no_need_paid.getCode().equals(refundOrder.getRepaymentState())
                || PayRefundOrder.RepaymentState.success.getCode().equals(refundOrder.getRepaymentState())){
            log.error("未回款，无法结清,{}",outTradeNo);
            throw new BizException("无法结清");
        }
        refundOrder.setRepaymentState(PayRefundOrder.RepaymentState.wait_success.getCode());
        this.updateById(refundOrder);
        myBankSupplyChainService.tradeRepay(refundOrder.getAuthNo());
    }

    @Override
    public PayRefundOrder queryByOutOrderNo(String outOrderNo) {
        PayRefundOrderQuery query = new PayRefundOrderQuery();
        query.setOutOrderNo(outOrderNo);
        List<PayRefundOrder> orderList = this.queryList(query);
        return orderList == null || orderList.size() == 0  ? null : orderList.get(0);
    }
}

package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import com.zanclick.prepay.order.mapper.PayRefundOrderMapper;
import com.zanclick.prepay.order.query.PayRefundOrderQuery;
import com.zanclick.prepay.order.service.PayRefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-14 11:30:42
 **/
@Service
public class PayRefundOrderServiceImpl extends BaseMybatisServiceImpl<PayRefundOrder,Long> implements PayRefundOrderService {

    @Autowired
    private PayRefundOrderMapper payRefundOrderMapper;


    @Override
    protected BaseMapper<PayRefundOrder, Long> getBaseMapper() {
        return payRefundOrderMapper;
    }

    @Override
    public PayRefundOrder queryByOutTradeNo(String outTradeNo) {
        PayRefundOrderQuery query = new PayRefundOrderQuery();
        query.setOutTradeNo(outTradeNo);
        List<PayRefundOrder> orderList = this.queryList(query);
        return orderList == null ? null : orderList.get(0);
    }

    @Override
    public PayRefundOrder queryByOutOrderNo(String outOrderNo) {
        PayRefundOrderQuery query = new PayRefundOrderQuery();
        query.setOutOrderNo(outOrderNo);
        List<PayRefundOrder> orderList = this.queryList(query);
        return orderList == null ? null : orderList.get(0);
    }
}

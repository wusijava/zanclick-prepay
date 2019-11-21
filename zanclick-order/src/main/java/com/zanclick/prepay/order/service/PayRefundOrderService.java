package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.PayRefundOrder;

/**
 * @author Administrator
 * @date 2019-11-14 11:30:42
 **/
public interface PayRefundOrderService extends BaseService<PayRefundOrder,Long> {

    /**
     * 根据 outOrderNo查找
     *
     * @param outTradeNo
     * @return
     */
    PayRefundOrder queryByOutTradeNo(String outTradeNo);


    /**
     * 根据 outOrderNo查找
     *
     * @param outTradeNo
     * @return
     */
    void settle(String outTradeNo);


    /**
     * 根据 outOrderNo查找
     *
     * @param outOrderNo
     * @return
     */
    PayRefundOrder queryByOutOrderNo(String outOrderNo);

}

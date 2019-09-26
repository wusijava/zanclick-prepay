package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
public interface PayOrderService extends BaseService<PayOrder,Long> {

    /**
     * 根据 orderNo查找
     *
     * @param orderNo
     * @return
     */
    PayOrder queryByOrderNo(String orderNo);

    /**
     * 根据 outOrderNo查找
     *
     * @param outOrderNo
     * @return
     */
    PayOrder queryByOutOrderNo(String outOrderNo);

}

package com.zanclick.prepay.settle.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.settle.entity.SettleOrder;

/**
 * @author Administrator
 * @date 2019-10-31 15:47:10
 **/
public interface SettleOrderService extends BaseService<SettleOrder,Long> {

    /**
     * 根据orderNo查询
     * @@param orderNo
     * */
    SettleOrder queryByOrderNo(String orderNo);


}

package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.SupplyChainBill;

/**
 * @author Administrator
 * @date 2019-10-29 17:12:22
 **/
public interface SupplyChainBillService extends BaseService<SupplyChainBill,Long> {

    SupplyChainBill queryByRequestId(String requestId);
}

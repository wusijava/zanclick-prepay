package com.zanclick.prepay.supplychain.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.supplychain.entity.SupplyChainBill;

/**
 * @author lvlu
 * @date 2019-05-10 15:23
 **/
public interface SupplyChainBillRecordService extends BaseService<SupplyChainBill,Long> {
    SupplyChainBill queryByRequestId(String requestId);
}

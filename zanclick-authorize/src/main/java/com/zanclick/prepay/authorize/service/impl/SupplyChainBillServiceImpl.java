package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.SupplyChainBill;
import com.zanclick.prepay.authorize.mapper.SupplyChainBillMapper;
import com.zanclick.prepay.authorize.service.SupplyChainBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-10-29 17:12:22
 **/
@Service
public class SupplyChainBillServiceImpl extends BaseMybatisServiceImpl<SupplyChainBill,Long> implements SupplyChainBillService {

    @Autowired
    private SupplyChainBillMapper supplyChainBillMapper;


    @Override
    protected BaseMapper<SupplyChainBill, Long> getBaseMapper() {
        return supplyChainBillMapper;
    }

    @Override
    public SupplyChainBill queryByRequestId(String requestId) {
        return null;
    }
}

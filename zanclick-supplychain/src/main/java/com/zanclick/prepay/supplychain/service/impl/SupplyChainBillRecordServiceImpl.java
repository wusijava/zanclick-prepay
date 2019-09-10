package com.zanclick.prepay.supplychain.service.impl;


import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.supplychain.entity.SupplyChainBill;
import com.zanclick.prepay.supplychain.mapper.SupplyChainBillMapper;
import com.zanclick.prepay.supplychain.service.SupplyChainBillRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lvlu
 * @date 2019-05-10 15:24
 **/
@Service
public class SupplyChainBillRecordServiceImpl extends BaseMybatisServiceImpl<SupplyChainBill,Long> implements SupplyChainBillRecordService {

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

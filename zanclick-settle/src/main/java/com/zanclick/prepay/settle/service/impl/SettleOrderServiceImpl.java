package com.zanclick.prepay.settle.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.settle.entity.SettleOrder;
import com.zanclick.prepay.settle.mapper.SettleOrderMapper;
import com.zanclick.prepay.settle.service.SettleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-10-31 15:47:10
 **/
@Service
public class SettleOrderServiceImpl extends BaseMybatisServiceImpl<SettleOrder,Long> implements SettleOrderService {

    @Autowired
    private SettleOrderMapper settleOrderMapper;

    @Override
    protected BaseMapper<SettleOrder, Long> getBaseMapper() {
        return settleOrderMapper;
    }

    @Override
    public SettleOrder queryByOrderNo(String orderNo) {
        return settleOrderMapper.selectByOrderNo(orderNo);
    }
}

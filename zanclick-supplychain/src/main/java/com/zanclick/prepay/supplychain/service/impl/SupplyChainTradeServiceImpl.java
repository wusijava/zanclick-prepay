package com.zanclick.prepay.supplychain.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import com.zanclick.prepay.supplychain.enums.TradeStateEnum;
import com.zanclick.prepay.supplychain.mapper.SupplyChainTradeMapper;
import com.zanclick.prepay.supplychain.service.SupplyChainTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lvlu
 * @date 2019-05-10 15:24
 **/
@Service
public class SupplyChainTradeServiceImpl extends BaseMybatisServiceImpl<SupplyChainTrade,Long> implements SupplyChainTradeService {


    @Autowired
    private SupplyChainTradeMapper supplyChainTradeMapper;

    @Override
    protected BaseMapper<SupplyChainTrade, Long> getBaseMapper() {
        return supplyChainTradeMapper;
    }

    @Override
    public SupplyChainTrade queryByRequestId(String requestId) {
        SupplyChainTrade query = new SupplyChainTrade();
        query.setRequestId(requestId);
        List<SupplyChainTrade> list = this.queryList(query,PageRequest.of(0,1));
        return DataUtil.isNotEmpty(list)?list.get(0):null;
    }

    @Override
    public SupplyChainTrade queryByAuthNoAndState(String auth_no, TradeStateEnum state) {
        SupplyChainTrade query = new SupplyChainTrade();
        query.setAuthNo(auth_no);
        query.setState(state);
        List<SupplyChainTrade> list = this.queryList(query,PageRequest.of(0,1));
        return DataUtil.isNotEmpty(list)?list.get(0):null;
    }

    @Override
    public SupplyChainTrade queryByAuthNo(String auth_no) {
        return supplyChainTradeMapper.selectByAuthNo(auth_no);
    }
}

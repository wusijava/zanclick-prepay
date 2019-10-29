package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import com.zanclick.prepay.authorize.mapper.SupplyChainTradeMapper;
import com.zanclick.prepay.authorize.service.SupplyChainTradeService;
import com.zanclick.prepay.common.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-10-29 17:16:41
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
        query.setState(state.getCode());
        List<SupplyChainTrade> list = this.queryList(query,PageRequest.of(0,1));
        return DataUtil.isNotEmpty(list)?list.get(0):null;
    }

    @Override
    public SupplyChainTrade queryByAuthNo(String auth_no) {
        return supplyChainTradeMapper.selectByAuthNo(auth_no);
    }

}

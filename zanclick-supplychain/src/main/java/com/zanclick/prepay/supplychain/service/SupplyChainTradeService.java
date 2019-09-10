package com.zanclick.prepay.supplychain.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import com.zanclick.prepay.supplychain.enums.TradeStateEnum;

/**
 * @author lvlu
 * @date 2019-05-10 15:23
 **/
public interface SupplyChainTradeService extends BaseService<SupplyChainTrade, Long> {
    /**
     * 根据requestId查找
     *
     * @param requestId
     * @return
     */
    SupplyChainTrade queryByRequestId(String requestId);

    /**
     * 根据 authNo和state查找
     *
     * @param auth_no
     * @param state
     * @return
     */
    SupplyChainTrade queryByAuthNoAndState(String auth_no, TradeStateEnum state);

    /**
     * 根据 authNo
     *
     * @param auth_no
     * @return
     */
    SupplyChainTrade queryByAuthNo(String auth_no);
}

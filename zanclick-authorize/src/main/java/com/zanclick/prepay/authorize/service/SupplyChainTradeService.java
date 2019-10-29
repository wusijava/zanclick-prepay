package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;

/**
 * @author Administrator
 * @date 2019-10-29 17:16:41
 **/
public interface SupplyChainTradeService extends BaseService<SupplyChainTrade,Long> {

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

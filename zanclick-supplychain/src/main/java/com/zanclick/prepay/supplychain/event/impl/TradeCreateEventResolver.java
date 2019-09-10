package com.zanclick.prepay.supplychain.event.impl;

import com.alibaba.fastjson.JSON;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import com.zanclick.prepay.supplychain.enums.TradeStateEnum;
import com.zanclick.prepay.supplychain.event.BaseEventResolver;
import com.zanclick.prepay.supplychain.event.domain.TradeCreateEvent;
import com.zanclick.prepay.supplychain.exception.SupplyChainException;
import com.zanclick.prepay.supplychain.service.SupplyChainTradeService;
import com.zanclick.prepay.supplychain.util.SupplyChainUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lvlu
 * @date 2019-05-10 11:58
 **/
@Slf4j
@Service(value = "tradeCreateEventResolver")
public class TradeCreateEventResolver implements BaseEventResolver {

    @Autowired
    private SupplyChainTradeService supplyChainTradeService;

    @Override
    public void resolveEvent(String dataObject) throws SupplyChainException {
        log.info("交易创建通知:{}",dataObject);
        TradeCreateEvent event = JSON.parseObject(dataObject,TradeCreateEvent.class);
        String out_order_no = event.getOutOrderNo();
        String requestId = event.getRequestId();
        String success = event.getSuccess();
        if(DataUtil.isEmpty(success) ||DataUtil.isEmpty(out_order_no) || DataUtil.isEmpty(requestId)){
            throw new SupplyChainException("参数不全");
        }
        String auth_no = SupplyChainUtils.getAuthNoFromOutOrderNo(out_order_no);
        if(auth_no == null){
            throw new SupplyChainException("out_order_no格式有误");
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByRequestId(requestId);
        if(DataUtil.isEmpty(trade)){
            return ;
        }
        if(!TradeStateEnum.CREATED.equals(trade.getState())){
            return ;
        }
        if(Boolean.TRUE.toString().equalsIgnoreCase(success)) {
            trade.setState(TradeStateEnum.WAIT_RECEIPT);
            supplyChainTradeService.updateById(trade);
        }else{
            trade.setState(TradeStateEnum.FAILED);
            trade.setFailReason(event.getErrorMsg());
            supplyChainTradeService.updateById(trade);
        }
    }
}

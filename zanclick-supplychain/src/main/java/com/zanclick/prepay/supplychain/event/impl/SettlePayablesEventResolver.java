package com.zanclick.prepay.supplychain.event.impl;

import com.alibaba.fastjson.JSON;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import com.zanclick.prepay.supplychain.enums.TradeStateEnum;
import com.zanclick.prepay.supplychain.event.BaseEventResolver;
import com.zanclick.prepay.supplychain.event.domain.SettlePayablesEvent;
import com.zanclick.prepay.supplychain.exception.SupplyChainException;
import com.zanclick.prepay.supplychain.service.SupplyChainBillRecordService;
import com.zanclick.prepay.supplychain.service.SupplyChainTradeService;
import com.zanclick.prepay.supplychain.util.SupplyChainUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019-05-10 11:58
 **/
@Slf4j
@Service(value = "settlePayablesEventResolver")
public class SettlePayablesEventResolver implements BaseEventResolver {

    @Autowired
    private SupplyChainTradeService supplyChainTradeService;
    @Autowired
    private SupplyChainBillRecordService supplyChainBillRecordService;


    @Override
    public void resolveEvent(String dataObject) throws SupplyChainException {
        log.info("结清消息通知:{}",dataObject);
        SettlePayablesEvent event = JSON.parseObject(dataObject,SettlePayablesEvent.class);
        String out_order_no = event.getOutOrderNo();
        String requestId = event.getRequestId();
        if(DataUtil.isEmpty(out_order_no) || DataUtil.isEmpty(requestId)){
            throw new SupplyChainException("参数不全");
        }
        String auth_no = SupplyChainUtils.getAuthNoFromOutOrderNo(out_order_no);
        if(auth_no == null){
            throw new SupplyChainException("out_order_no格式有误");
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByAuthNoAndState(auth_no, TradeStateEnum.LOANED);
        if(DataUtil.isEmpty(trade)){
            return ;
        }
        if(!TradeStateEnum.LOANED.equals(trade.getState())){
            return ;
        }
        trade.setState(TradeStateEnum.FINISHED);
        trade.setFinishTime(new Date());
        supplyChainTradeService.updateById(trade);
    }
}

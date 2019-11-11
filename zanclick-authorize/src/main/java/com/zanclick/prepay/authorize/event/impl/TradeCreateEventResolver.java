package com.zanclick.prepay.authorize.event.impl;

import com.alibaba.fastjson.JSON;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.authorize.event.AbstractBaseEventResolver;
import com.zanclick.prepay.authorize.event.BaseEventResolver;
import com.zanclick.prepay.authorize.event.domain.TradeCreateEvent;
import com.zanclick.prepay.authorize.exception.SupplyChainException;
import com.zanclick.prepay.authorize.service.SupplyChainTradeService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.common.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lvlu
 * @date 2019-05-10 11:58
 **/
@Slf4j
@Service(value = "tradeCreateEventResolver")
public class TradeCreateEventResolver extends AbstractBaseEventResolver implements BaseEventResolver {

    @Autowired
    private SupplyChainTradeService supplyChainTradeService;

    @Override
    public void resolveEvent(String dataObject) throws SupplyChainException {
        log.info("交易创建通知:{}", dataObject);
        TradeCreateEvent event = JSON.parseObject(dataObject, TradeCreateEvent.class);
        String out_order_no = event.getOutOrderNo();
        String requestId = event.getRequestId();
        String success = event.getSuccess();
        if (DataUtil.isEmpty(success) || DataUtil.isEmpty(out_order_no) || DataUtil.isEmpty(requestId)) {
            throw new SupplyChainException("参数不全");
        }
        String auth_no = SupplyChainUtils.getAuthNoFromOutOrderNo(out_order_no);
        if (auth_no == null) {
            throw new SupplyChainException("out_order_no格式有误");
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByRequestId(requestId);
        if (DataUtil.isEmpty(trade)) {
            return;
        }
        if (!TradeStateEnum.CREATED.getCode().equals(trade.getState())) {
            return;
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(success)) {
            trade.setState(TradeStateEnum.WAIT_RECEIPT.getCode());
            supplyChainTradeService.updateById(trade);
        } else {
            trade.setState(TradeStateEnum.FAILED.getCode());
            trade.setFailReason(event.getErrorMsg());
            supplyChainTradeService.updateById(trade);
            sendMessage(1,event.getErrorMsg(),trade.getAuthNo());
        }
    }

}

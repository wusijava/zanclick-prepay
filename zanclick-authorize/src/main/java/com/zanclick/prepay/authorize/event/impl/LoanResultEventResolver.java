package com.zanclick.prepay.authorize.event.impl;

import com.alibaba.fastjson.JSON;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.authorize.event.BaseEventResolver;
import com.zanclick.prepay.authorize.event.domain.LoanResultEvent;
import com.zanclick.prepay.authorize.exception.SupplyChainException;
import com.zanclick.prepay.authorize.service.SupplyChainTradeService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.common.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019-05-10 11:58
 **/
@Slf4j
@Service(value = "loanResultEventResolver")
public class LoanResultEventResolver implements BaseEventResolver {

    @Autowired
    private SupplyChainTradeService supplyChainTradeService;

    @Override
    public void resolveEvent(String dataObject) throws SupplyChainException {
        log.info("放款消息通知:{}",dataObject);
        LoanResultEvent event = JSON.parseObject(dataObject,LoanResultEvent.class);
        String out_order_no = event.getOutOrderNo();
        String success = event.getSuccess();
        if(DataUtil.isEmpty(success) || DataUtil.isEmpty(out_order_no)){
            throw new SupplyChainException("参数不全");
        }
        String auth_no = SupplyChainUtils.getAuthNoFromOutOrderNo(out_order_no);
        if(auth_no == null){
            throw new SupplyChainException("out_order_no格式有误");
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByAuthNoAndState(auth_no, TradeStateEnum.WAIT_RECEIPT);
        if(DataUtil.isEmpty(trade)){
            return ;
        }
        if(!TradeStateEnum.WAIT_RECEIPT.getCode().equals(trade.getState())){
            return ;
        }
        if(Boolean.TRUE.toString().equalsIgnoreCase(success)){
            trade.setState(TradeStateEnum.LOANED.getCode());
            trade.setFactoringAmt(event.getFactoringAmt());
            trade.setFactoringNo(event.getFactoringNo());
            trade.setTradeNo(event.getTradeNo());
            trade.setCurrency(event.getCurrency());
            trade.setLoanTime(new Date());
            supplyChainTradeService.updateById(trade);
        }else{
            log.error("out_order_no:{}放款失败,原因为：{}",out_order_no,event.getErrorMsg());
        }
    }
}

package com.zanclick.prepay.authorize.event.impl;

import com.alibaba.fastjson.JSON;
import com.zanclick.prepay.authorize.entity.SupplyChainBill;
import com.zanclick.prepay.authorize.enums.BillStateEnum;
import com.zanclick.prepay.authorize.event.BaseEventResolver;
import com.zanclick.prepay.authorize.event.domain.TradePayEvent;
import com.zanclick.prepay.authorize.exception.SupplyChainException;
import com.zanclick.prepay.authorize.service.SupplyChainBillService;
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
@Service(value = "tradePayEventResolver")
public class TradePayEventResolver implements BaseEventResolver {

    @Autowired
    private SupplyChainBillService supplyChainBillService;

    @Override
    public void resolveEvent(String dataObject) throws SupplyChainException {
        log.info("交易支付结果消息:{}",dataObject);
        TradePayEvent event = JSON.parseObject(dataObject,TradePayEvent.class);
        String out_order_no = event.getOutOrderNo();
        String success = event.getSuccess();
        String requestId = event.getRequestId();
        if(DataUtil.isEmpty(success) || DataUtil.isEmpty(out_order_no) || DataUtil.isEmpty(requestId)){
            throw new SupplyChainException("参数不全");
        }
        String auth_no = SupplyChainUtils.getAuthNoFromOutOrderNo(out_order_no);
        if(auth_no == null){
            throw new SupplyChainException("out_order_no格式有误");
        }
        SupplyChainBill bill = supplyChainBillService.queryByRequestId(requestId);
        if(DataUtil.isEmpty(bill)){
            return ;
        }
        if(!BillStateEnum.CREATED.getCode().equals(bill.getState())){
            return ;
        }

        if(Boolean.TRUE.toString().equalsIgnoreCase(success)) {
            bill.setState(BillStateEnum.SUCC.getCode());
            supplyChainBillService.updateById(bill);
        }else{
            bill.setState(BillStateEnum.FAILED.getCode());
            bill.setFailReason(event.getErrorMsg());
            supplyChainBillService.updateById(bill);
        }
    }
}

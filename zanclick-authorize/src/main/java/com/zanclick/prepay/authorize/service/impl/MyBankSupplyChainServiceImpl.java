package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.response.MybankCreditSupplychainTradeBillrepaybudgetQueryResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.SupplyChainBill;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import com.zanclick.prepay.authorize.enums.BillStateEnum;
import com.zanclick.prepay.authorize.enums.TradeStateEnum;
import com.zanclick.prepay.authorize.exception.SupplyChainException;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.MyBankSupplyChainService;
import com.zanclick.prepay.authorize.service.SupplyChainBillService;
import com.zanclick.prepay.authorize.service.SupplyChainTradeService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.SupplyChainCreate;
import com.zanclick.prepay.authorize.vo.SupplyChainPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * @author lvlu
 * @date 2019-05-07 14:09
 **/
@Component
@Slf4j
public class MyBankSupplyChainServiceImpl implements MyBankSupplyChainService {

    @Autowired
    private SupplyChainTradeService supplyChainTradeService;

    @Autowired
    private SupplyChainBillService supplyChainBillService;

    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void tradeCreate(SupplyChainTrade trade) {
        trade.setState(TradeStateEnum.CREATED.getCode());
        supplyChainTradeService.insert(trade);
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(trade.getConfigurationId());
        try {
            String requestId = SupplyChainUtils.tradeCreate(chainCreate(trade),configuration);
            trade.setRequestId(requestId);
            supplyChainTradeService.updateById(trade);
        }catch (SupplyChainException e){
            String requestId = e.getRequestId();
            trade.setRequestId(requestId);
            trade.setFailReason(e.getMsg());
            trade.setState(TradeStateEnum.FAILED.getCode());
            supplyChainTradeService.updateById(trade);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String tradeCancel(String auth_no){
        SupplyChainTrade trade = supplyChainTradeService.queryByAuthNoAndState(auth_no,null);
        try {
            trade.setState(TradeStateEnum.TOCANCEL.getCode());
            supplyChainTradeService.updateById(trade);
            return "ok";
        }catch (SupplyChainException e){
            log.error("取消交易失败，失败原因：{}",e.getMsg());
            return e.getMsg();
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void tradeRepay(String auth_no){
        MybankCreditSupplychainTradeBillrepaybudgetQueryResponse response = SupplyChainUtils.tradeBillRepayBudgetQuery(auth_no,null);
        if(response.isSuccess()){
            SupplyChainBill bill = new SupplyChainBill();
            bill.setTotalAmount(response.getTotalAmt());
            bill.setInstallNum("0");
            bill.setDueDate(null);
            bill.setStartDate(null);
            bill.setAuthNo(auth_no);
            bill.setCreateTime(new Date());
            bill.setAmount(response.getBillAmtDetail().getPrin());
            bill.setFee(response.getBillAmtDetail().getFee());
            bill.setState(BillStateEnum.CREATED.getCode());
            supplyChainBillService.insert(bill);
            try {
                SupplyChainPay pay = new SupplyChainPay();
                pay.setAuthNo(auth_no);
                pay.setPayAmount(response.getTotalAmt());
                String requestId = SupplyChainUtils.tradePay(pay,null);
                bill.setRequestId(requestId);
                supplyChainBillService.updateById(bill);
            }catch (SupplyChainException e){
                updateBillAsCatchASupplyChainException(bill, e);
            }
        }
    }


    private void updateBillAsCatchASupplyChainException(SupplyChainBill bill, SupplyChainException e) {
        String requestId = e.getRequestId();
        bill.setRequestId(requestId);
        bill.setFailReason(e.getMsg());
        bill.setState(BillStateEnum.UNCREATED.getCode());
        supplyChainBillService.updateById(bill);
    }

    private SupplyChainCreate chainCreate(SupplyChainTrade trade) {
        SupplyChainCreate create = new SupplyChainCreate();
        create.setAuthNo(trade.getAuthNo());
        create.setFreezeAliPayId(trade.getFreezeUserId());
        create.setOutMerOrderNo(trade.getOutTradeNo());
        create.setOperationId(trade.getOpId());
        create.setOutRequestNo(trade.getOutRequestNo());
        create.setAmount(trade.getAmount());
        create.setFqNum(trade.getFqNum());
        create.setFreezeDate(trade.getFreezeDate());
        create.setExpireDate(trade.getExpireDate());
        create.setTitle(trade.getTitle());
        create.setRcvLoginId(trade.getRcvLoginId());
        create.setRcvAliPayName(trade.getRcvAlipayName());
        create.setRcvContactName(trade.getRcvContactName());
        create.setRcvContactPhone(trade.getRcvContactPhone());
        create.setRcvContactEmail(trade.getRcvContactEmail());
        return create;
    }

}

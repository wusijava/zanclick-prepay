package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.MyBankSupplyChainService;
import com.zanclick.prepay.authorize.vo.QueryDTO;
import com.zanclick.prepay.authorize.vo.QueryResult;
import com.zanclick.prepay.authorize.vo.Refund;
import com.zanclick.prepay.authorize.vo.RefundResult;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.vo.RedPacketList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
@Slf4j
@Service
public class PayOrderServiceImpl extends BaseMybatisServiceImpl<PayOrder, Long> implements PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private MyBankSupplyChainService myBankSupplyChainService;

    @Override
    protected BaseMapper<PayOrder, Long> getBaseMapper() {
        return payOrderMapper;
    }

    @Override
    public PayOrder queryByOutTradeNo(String orderNo) {
        return payOrderMapper.selectByOutTradeNo(orderNo);
    }

    @Override
    public PayOrder queryByOutOrderNo(String outOrderNo) {
        return payOrderMapper.selectByOutOrderNo(outOrderNo);
    }

    @Override
    public PayOrder queryByAuthNo(String authNo) {
        return payOrderMapper.selectByAuthNo(authNo);
    }

    @Override
    public PayOrder queryAndHandlePayOrder(String outTradeNo, String outOrderNo) {
        PayOrder payOrder = null;
        if (DataUtil.isNotEmpty(outOrderNo)) {
            payOrder = this.queryByOutTradeNo(outOrderNo);
        }
        if (DataUtil.isEmpty(payOrder) && DataUtil.isNotEmpty(outOrderNo)) {
            payOrder = this.queryByOutOrderNo(outOrderNo);
        }
        if (payOrder == null) {
            log.error("订单信息异常:{},{}", outOrderNo, outTradeNo);
            throw new BizException("订单信息异常");
        }
        if (payOrder.isWait() && payOrder.getRequestNo() != null && payOrder.getQrCodeUrl() != null) {
            QueryDTO dto = new QueryDTO();
            dto.setOutTradeNo(payOrder.getOutTradeNo());
            QueryResult queryResult = authorizePayService.query(dto);
            if (queryResult.isSuccess()) {
                if (AuthorizeOrder.State.payed.getCode().equals(queryResult.getState())) {
                    payOrder.setState(PayOrder.State.payed.getCode());
                    payOrder.setFinishTime(new Date());
                    handlePayOrder(payOrder);
                } else if (AuthorizeOrder.State.failed.getCode().equals(queryResult.getState()) || AuthorizeOrder.State.closed.getCode().equals(queryResult.getState())) {
                    payOrder.setRequestNo(null);
                    payOrder.setQrCodeUrl(null);
                    payOrder.setState(PayOrder.State.closed.getCode());
                    payOrder.setFinishTime(new Date());
                    handlePayOrder(payOrder);
                }
            } else {
                log.error("交易信息查询异常:{},{},{}", queryResult.getMessage(), outOrderNo, outTradeNo);
                payOrder.setRequestNo(null);
                payOrder.setQrCodeUrl(null);
                payOrder.setState(PayOrder.State.closed.getCode());
                payOrder.setFinishTime(new Date());
                handlePayOrder(payOrder);
            }
        }
        return payOrder;
    }

    @Override
    public void handlePayOrder(PayOrder order) {
        if (order.isPayed()) {
            sendMessage(order);
        }
        this.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleSuccess(String outTradeNo, String authNo) {
        PayOrder payOrder = payOrderMapper.selectByOutTradeNo(outTradeNo);
        if (payOrder == null) {
            log.error("交易订单异常:{}", outTradeNo);
            return;
        }
        payOrder.setAuthNo(authNo);
        payOrder.setState(PayOrder.State.payed.getCode());
        payOrder.setFinishTime(new Date());
        handlePayOrder(payOrder);
    }

    @Override
    public void settle(String outTradeNo) {
        PayOrder order = payOrderMapper.selectByOutTradeNo(outTradeNo);
        if (order.getDealState().equals(PayOrder.DealState.settled.getCode())
                || order.getDealState().equals(PayOrder.DealState.settle_wait.getCode())) {
            log.error("订单状态异常:{},{}", outTradeNo, order.getDealStateDesc());
            throw new BizException("订单状态异常");
        }
        sendMessage(order);
    }

    @Override
    public void sendMessage(PayOrder order) {
        SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE, order.getOutTradeNo());
    }

    @Override
    public synchronized void refund(String outTradeNo, Integer type) {
        PayOrder order = payOrderMapper.selectByOutTradeNo(outTradeNo);
        if (order == null || !order.isPayed()){
            log.error("订单状态异常,无法退款:{},{}",outTradeNo,type);
            throw new BizException("订单状态异常,无法退款");
        }
        Refund refund = new Refund();
        refund.setType(0);
        refund.setAmount(order.getAmount());
        refund.setReason("移动套餐退款_"+order.getOutOrderNo());
        refund.setOutRequestNo(StringUtils.getTradeNo());
        refund.setOutTradeNo(order.getOutTradeNo());
        RefundResult result = authorizePayService.refund(refund);
        if (result.isSuccess()){
            order.setState(PayOrder.State.refund.getCode());
            payOrderMapper.updateById(order);
            if (type.equals(0)){
                myBankSupplyChainService.tradeRepay(order.getAuthNo());
            }
        }else {
            log.error("退款失败:{},{}",outTradeNo,type,result.getMessage());
            throw new BizException(result.getMessage());
        }
    }


}

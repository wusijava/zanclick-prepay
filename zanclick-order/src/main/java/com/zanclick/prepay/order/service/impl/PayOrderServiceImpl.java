package com.zanclick.prepay.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.vo.QueryDTO;
import com.zanclick.prepay.authorize.vo.QueryResult;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public PayOrder queryAndHandlePayOrder(String outTradeNo, String outOrderNo) {
        PayOrder payOrder = null;
        if (DataUtil.isNotEmpty(outOrderNo)){
            payOrder = this.queryByOutTradeNo(outOrderNo);
        }
        if (DataUtil.isEmpty(payOrder) && DataUtil.isNotEmpty(outOrderNo)){
            payOrder = this.queryByOutOrderNo(outOrderNo);
        }
        if (payOrder == null){
            log.error("订单信息异常:{},{}",outOrderNo,outTradeNo);
            throw new BizException("订单信息异常");
        }
        if (payOrder.isWait() && payOrder.getRequestNo() != null && payOrder.getQrCodeUrl() !=null){
            QueryDTO dto = new QueryDTO();
            dto.setOutTradeNo(payOrder.getOutTradeNo());
            QueryResult queryResult = authorizePayService.query(dto);
            if (queryResult.isSuccess()){
                if (AuthorizeOrder.State.payed.getCode().equals(queryResult.getState())){
                    payOrder.setState(PayOrder.State.payed.getCode());
                    payOrder.setFinishTime(new Date());
                    handlePayOrder(payOrder);
                }else if (AuthorizeOrder.State.failed.getCode().equals(queryResult.getState()) || AuthorizeOrder.State.closed.getCode().equals(queryResult.getState())){
                    payOrder.setState(PayOrder.State.closed.getCode());
                    payOrder.setFinishTime(new Date());
                    handlePayOrder(payOrder);
                }
            }else {
                log.error("交易信息异常:{},{},{}",queryResult.getMessage(),outOrderNo,outTradeNo);
                throw new RuntimeException(queryResult.getMessage());
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
    public void handleSuccess(String outTradeNo,String authNo) {
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
    public void sendMessage(PayOrder order) {
        SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE,JSONObject.toJSONString(order));
    }



}

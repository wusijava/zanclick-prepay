package com.zanclick.prepay.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.authorize.vo.Query;
import com.zanclick.prepay.authorize.vo.QueryResult;
import com.zanclick.prepay.authorize.vo.Refund;
import com.zanclick.prepay.authorize.vo.RefundResult;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.PayRefundOrderService;
import com.zanclick.prepay.order.service.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private PayRefundOrderService payRefundOrderService;
    @Autowired
    private RedPacketService redPacketService;

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
    public PayOrder queryRedPacketOrder(String outOrderNo) {
        PayOrder order = this.queryByOutOrderNo(outOrderNo);
        if (order == null) {
            log.error("订单编号错误:{}", outOrderNo);
            throw new BizException("订单编号错误");
        }
        if (order.isWait()) {
            throw new BizException("订单未支付，无法领取红包");
        }
        if (order.isClosed()) {
            throw new BizException("订单已关闭，无法领取红包");
        }
        if (order.isRefund()) {
            throw new BizException("订单已退款，无法领取红包");
        }
        if (PayOrder.RedPackState.receive.getCode().equals(order.getRedPackState())) {
            throw new BizException("该笔订单红包已被领取");
        }
        if (!MoneyUtil.largeMoney(order.getRedPackAmount(), "0.00")) {
            throw new BizException("该笔订单可领取红包金额为0.00");
        }
        return order;
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
        JSONObject object = null;
        if (payOrder.isWait() && payOrder.getRequestNo() != null && payOrder.getQrCodeUrl() != null) {
            Query dto = new Query();
            dto.setOutTradeNo(payOrder.getOutTradeNo());
            QueryResult queryResult = authorizePayService.query(dto);
            if (queryResult.isSuccess()) {
                if (AuthorizeOrder.State.payed.getCode().equals(queryResult.getState())) {
                    payOrder.setState(PayOrder.State.payed.getCode());
                    payOrder.setFinishTime(new Date());
                    payOrder.setAuthNo(queryResult.getAuthNo());
                    payOrder.setBuyerNo(queryResult.getBuyerNo());
                    object = new JSONObject();
                } else if (AuthorizeOrder.State.failed.getCode().equals(queryResult.getState()) || AuthorizeOrder.State.closed.getCode().equals(queryResult.getState())) {
                    payOrder.setRequestNo(null);
                    payOrder.setQrCodeUrl(null);
                    payOrder.setState(PayOrder.State.closed.getCode());
                    payOrder.setFinishTime(new Date());
                    object = new JSONObject();
                }
            }
        }
        if (object != null){
            object.put("outTradeNo",payOrder.getOutTradeNo());
            object.put("state",payOrder.getState());
            if (DataUtil.isNotEmpty(payOrder.getAuthNo())){
                object.put("authNo",payOrder.getAuthNo());
            }
            if (DataUtil.isNotEmpty(payOrder.getBuyerNo())){
                object.put("buyerNo",payOrder.getBuyerNo());
            }
            SendMessage.sendMessage(JmsMessaging.ORDER_STATE_MESSAGE, object.toJSONString());
            syncQueryState(payOrder.getOutTradeNo(),payOrder.getState());
        }
        return payOrder;
    }

    @Override
    public void handlePayOrder(PayOrder order) {
        if (order.isPayed()) {
            SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE, order.getOutTradeNo());
            redPacketService.createRedPacket(order);
        }
        if (order.isRefund()){
            PayRefundOrder refundOrder = payRefundOrderService.queryByOutTradeNo(order.getOutTradeNo());
            refundOrder.setState(PayRefundOrder.State.success.getCode());
            refundOrder.setFinishTime(new Date());
            payRefundOrderService.updateById(refundOrder);
            redPacketService.refundRedPacket(order);
        }
        this.updateById(order);
    }

    @Override
    public void handleDealState(PayOrder order) {
        this.updateById(order);
        if (PayOrder.DealState.repayment_success.getCode().equals(order.getDealState())){
            PayRefundOrder refundOrder = payRefundOrderService.queryByOutTradeNo(order.getOutTradeNo());
            refundOrder.setRepaymentState(PayRefundOrder.RepaymentState.success.getCode());
            payRefundOrderService.updateById(refundOrder);
        }
    }

    @Override
    public String syncQueryDealState(String outTradeNo, Integer dealState) {
        int times = 30;
        for (int i = 0; i <= times; i++) {
            PayOrder order = this.queryByOutTradeNo(outTradeNo);
            if (!dealState.equals(order.getDealState())) {
                return null;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "退款超时，请稍后再试";
    }

    @Override
    public String syncQueryState(String outTradeNo, Integer state) {
        int times = 30;
        for (int i = 0; i <= times; i++) {
            PayOrder order = this.queryByOutTradeNo(outTradeNo);
            if (state.equals(order.getState())) {
                return null;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "处理超时";
    }

    @Override
    public String refund(String outTradeNo, String outOrderNo) {
        PayOrder order = null;
        if (outTradeNo != null){
            order = this.queryByOutTradeNo(outTradeNo);
        }
        if (order == null && outOrderNo != null){
            order = this.queryByOutOrderNo(outOrderNo);
        }
        return refund(order);
    }

    @Override
    public String cancel(PayOrder order) {
        return null;
    }

    @Override
    public String refund(PayOrder order) {
        if (order == null || !order.isPayed()) {
            log.error("订单状态异常,无法退款:{},{}", order.getOutTradeNo());
            return "订单状态异常,无法退款";
        }
        PayRefundOrder refundOrder = getPayRefundOrder(order);
        if (refundOrder.getState().equals(PayRefundOrder.State.wait.getCode())) {
            if (refundOrder.getRedPacketState().equals(PayRefundOrder.RedPacketState.receive.getCode())) {
                return "红包金额为退还，请退还后再行退款";
            }
            if (refundOrder.getRepaymentState().equals(PayRefundOrder.RepaymentState.no_paid.getCode())) {
                return "垫资金额未退还，请退还后再行退款";
            }
        } else if (refundOrder.getState().equals(PayRefundOrder.State.success.getCode())) {
            return "该笔订单已退款完成，无法再次退款";
        }
        String reason = refund(refundOrder);
        if (reason != null){
            return reason;
        }
        JSONObject object = new JSONObject();
        object.put("outTradeNo",order.getOutTradeNo());
        object.put("state",PayOrder.State.refund.getCode());
        SendMessage.sendMessage(JmsMessaging.ORDER_STATE_MESSAGE, object.toJSONString());
        syncQueryState(order.getOutTradeNo(),PayOrder.State.refund.getCode());
        return null;
    }

    @Override
    public void updateBySellerNo(PayOrder order) {
        payOrderMapper.updateBySellerNo(order);
    }


    /**
     * 解冻相关操作
     *
     * @param refundOrder
     */
    private String refund(PayRefundOrder refundOrder) {
        Refund refund = new Refund();
        refund.setType(0);
        refund.setAmount(refundOrder.getAmount());
        refund.setReason("移动套餐退款_" + refundOrder.getOutOrderNo());
        refund.setOutRequestNo(refundOrder.getOutRequestNo());
        refund.setOutTradeNo(refundOrder.getOutTradeNo());
        RefundResult result = authorizePayService.refund(refund);
        if (result.isSuccess()) {
            return null;
        }
        log.error("退款失败:{},{}", refundOrder.getOutTradeNo(), result.getMessage());
        return result.getMessage();
    }


    /**
     * 创建退款订单
     *
     * @param order
     */
    private PayRefundOrder getPayRefundOrder(PayOrder order) {
        PayRefundOrder refundOrder = payRefundOrderService.queryByOutTradeNo(order.getOutTradeNo());
        if (refundOrder == null) {
            refundOrder = createPayRefundOrder(order);
            payRefundOrderService.insert(refundOrder);
        } else if (refundOrder.getState().equals(PayRefundOrder.State.closed.getCode())) {
            PayRefundOrder refund = createPayRefundOrder(order);
            refund.setId(refundOrder.getId());
            payRefundOrderService.updateById(refund);
            return refund;
        }
        return refundOrder;
    }


    /**
     * 创建退款订单
     *
     * @param order
     */
    private PayRefundOrder createPayRefundOrder(PayOrder order) {
        PayRefundOrder refundOrder = new PayRefundOrder();
        refundOrder.setAmount(order.getAmount());
        refundOrder.setAppId(order.getAppId());
        refundOrder.setCreateTime(new Date());
        refundOrder.setAuthNo(order.getAuthNo());
        refundOrder.setMerchantNo(order.getMerchantNo());
        refundOrder.setOutOrderNo(order.getOutOrderNo());
        refundOrder.setOutTradeNo(order.getOutTradeNo());
        refundOrder.setOutRequestNo(StringUtils.getTradeNo());
        refundOrder.setRedPacketAmount(order.getRedPackAmount());
        refundOrder.setRedPacketState(getRedPacketState(order.getRedPackState()));
        refundOrder.setRepaymentState(getRepaymentState(order.getDealState()));
        refundOrder.setState(PayRefundOrder.State.wait.getCode());
        refundOrder.setWayId(order.getWayId());
        refundOrder.setSellerNo(order.getSellerNo());
        refundOrder.setSellerName(order.getName());
        refundOrder.setDealTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        return refundOrder;
    }

    /**
     * 获取当前订单的打款状态
     *
     * @param state
     */
    private Integer getRepaymentState(Integer state) {
        if (PayOrder.DealState.settled.getCode().equals(state)) {
            //TODO 这里改成默认已回款
            return PayRefundOrder.RepaymentState.paid.getCode();
        } else {
            return PayRefundOrder.RepaymentState.no_need_paid.getCode();
        }
    }

    /**
     * 获取红包领取状态
     *
     * @param state
     */
    private Integer getRedPacketState(Integer state) {
        if (PayOrder.RedPackState.un_receive.getCode().equals(state)) {
            //TODO 这里改成默认已回款
            return PayRefundOrder.RedPacketState.un_receive.getCode();
        } else {
            return PayRefundOrder.RedPacketState.refund.getCode();
        }
    }
}

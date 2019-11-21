package com.zanclick.prepay.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.Settle;
import com.zanclick.prepay.authorize.vo.SettleResult;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.RespInfo;
import com.zanclick.prepay.common.api.RestConfig;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 商户进阶通过，
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderNotifyListener {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private PayOrderService payOrderService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @JmsListener(destination = JmsMessaging.ORDER_NOTIFY_MESSAGE)
    public void getMessage(String message) {
        PayOrder order = payOrderService.queryByOutTradeNo(message);
        if (order == null){
            log.error("数据有问题:{}", order.getRequestNo());
            return;
        }
        if (order.getDealState().equals(PayOrder.DealState.settled.getCode())) {
            log.error("已经结算完成:{}", order.getRequestNo());
            return;
        }
        if (order.getDealState().equals(PayOrder.DealState.settle_wait.getCode())) {
            log.error("请等待结算操作:{}", order.getRequestNo());
            return;
        }
        if (order.getDealState().equals(PayOrder.DealState.notice_wait.getCode()) || order.getDealState().equals(PayOrder.DealState.notice_fail.getCode())) {
            AsiaInfoHeader header = AsiaInfoUtil.header(getRouteValue(order));
            JSONObject object = new JSONObject();
            object.put("orderNo", order.getOutTradeNo());
            object.put("outOrderNo", order.getOutOrderNo());
            object.put("packageNo", order.getPackageNo());
            object.put("payTime", sdf.format(order.getFinishTime()));
            object.put("merchantNo", order.getMerchantNo());
            object.put("orderStatus", getOrderStatus(order.getState()));
            String result = RestHttpClient.post(header, object.toJSONString(), RestConfig.payOrderNotify);
            log.error("通知结果：{}", result);
            if (result == null) {
                order.setDealState(PayOrder.DealState.notice_fail.getCode());
                order.setReason("未知错误");
                payOrderService.handleDealState(order);
            }
            try {
                RespInfo info = JSONObject.parseObject(result, RespInfo.class);
                if (!info.isSuccess()) {
                    log.error("能力系统异常:{},{}", order.getRequestNo(), info.getRespdesc());
                    order.setDealState(PayOrder.DealState.notice_fail.getCode());
                    order.setReason(info.getRespdesc());
                    payOrderService.handleDealState(order);
                    return;
                }
                if (!info.getResult().isSuccess()) {
                    log.error("能力业务异常:{},{}", order.getRequestNo(), info.getResult().getRetmsg());
                    order.setDealState(PayOrder.DealState.notice_fail.getCode());
                    order.setReason(info.getResult().getRetmsg());
                    payOrderService.handleDealState(order);
                    return;
                }
            } catch (Exception e) {
                log.error("通知结果转换出错:{},{},{}", order.getRequestNo(), result, e);
                order.setDealState(PayOrder.DealState.notice_fail.getCode());
                order.setReason("通知结果转换出错");
                payOrderService.handleDealState(order);
                return;
            }
        }
        settle(order);
    }

    /**
     * 通知成功，开始结算
     *
     * @param order
     */
    private void settle(PayOrder order) {
        try {
            if (order.getDealState().equals(PayOrder.DealState.notice_wait.getCode()) || order.getDealState().equals(PayOrder.DealState.notice_fail.getCode())) {
                AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(order.getMerchantNo());
                if (DateUtil.isSameDay(merchant.getCreateTime(), new Date())) {
                    order.setDealState(PayOrder.DealState.today_sign.getCode());
                    order.setReason("当天签约，无法打款");
                    payOrderService.handleDealState(order);
                    return;
                }
            }
            Settle dto = new Settle();
            dto.setAmount(order.getSettleAmount());
            dto.setOutTradeNo(order.getOutTradeNo());
            SettleResult settleResult = authorizePayService.settle(dto);
            if (settleResult.isSuccess()) {
                order.setDealState(PayOrder.DealState.settle_wait.getCode());
                order.setReason("等待结算");
            } else {
                order.setDealState(PayOrder.DealState.settle_fail.getCode());
                order.setReason(settleResult.getMessage());
            }
            payOrderService.handleDealState(order);
        }catch (Exception e){
            log.error("结算失败:{},{}",order.getOutTradeNo(),e);
        }
    }

    /**
     * 获取路由类型
     *
     * @param order
     * @return
     */
    private String getRouteValue(PayOrder order) {
        String routeValue = null;
        if (RestConfig.isPhone()) {
            routeValue = order.getPhoneNumber();
        } else {
            routeValue = order.getCity();
        }
        return routeValue;
    }


    /**
     * 获取交易状态
     *
     * @param state
     * @return
     */
    private String getOrderStatus(Integer state) {
        if (PayOrder.State.wait.getCode().equals(state)) {
            return "WAIT_PAY";
        } else if (PayOrder.State.payed.getCode().equals(state)) {
            return "TRADE_SUCCESS";
        } else if (PayOrder.State.refund.getCode().equals(state)) {
            return "TRADE_REFUND";
        }
        return "TRADE_CLOSED";
    }
}


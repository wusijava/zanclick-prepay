package com.zanclick.prepay.web.api.h5;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.vo.Refund;
import com.zanclick.prepay.authorize.vo.RefundResult;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.ApiPayRefund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 退款目前也是直接退的底层，需要调整
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickRefundAuthPay")
public class AuthPayRefundServiceImpl extends AbstractCommonService implements ApiRequestResolver {
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private PayOrderService payOrderService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("退款成功");
        try {
            verifyCipherJson(appId, cipherJson);
            ApiPayRefund query = parser(request, ApiPayRefund.class);
            PayOrder order = queryByOutTradeNoOrOutOrderNo(query.getOrderNo(),query.getOutOrderNo());
            refundOrder(order);
            param.setData(getRefundResult(order));
            return param.toString();
        } catch (BizException be) {
            param.setMessage(be.getMessage());
            log.error("查询异常:{}", be);
        } catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}", e);
        }
        param.setFail();
        return param.toString();
    }


    /**
     * 退款订单相关
     * @param order
     * */
    private JSONObject getRefundResult(PayOrder order){
        JSONObject object = new JSONObject();
        object.put("orderNo", order.getOutTradeNo());
        object.put("orderStatus", "PAY_REFUND");
        return object;
    }

    /**
     * 退款订单相关
     * @param order
     * */
    private void refundOrder(PayOrder order){
        if (!order.isPayed()){
            log.error("交易状态异常:{}",order.getOutTradeNo());
            throw new BizException("交易状态异常");
        }
        Refund dto = new Refund();
        dto.setOutTradeNo(order.getOutTradeNo());
        dto.setOutRequestNo(StringUtils.getTradeNo());
        dto.setAmount(order.getAmount());
        dto.setType(0);
        dto.setReason("交易退款");
        RefundResult result = authorizePayService.refund(dto);
        if (result.isSuccess()) {
            order.setState(PayOrder.State.refund.getCode());
            payOrderService.handlePayOrder(order);
            return;
        }
        log.error("交易退款失败:{},{}",order.getOutTradeNo(),result.getMessage());
        throw new BizException("result.getMessage()");
    }


    /**
     * 根据订单号查询
     * @param outTradeNo
     * @param outOrderNo
     * @return
     * */
    private PayOrder queryByOutTradeNoOrOutOrderNo(String outTradeNo,String outOrderNo){
        PayOrder order = null;
        if (outTradeNo != null){
            order = payOrderService.queryByOutTradeNo(outTradeNo);
        }
        if (order == null && outOrderNo != null){
            order = payOrderService.queryByOutOrderNo(outOrderNo);
        }
        if (order == null){
            log.error("交易订单号异常:{},{}",outTradeNo,outOrderNo);
            throw new BizException("交易订单号异常");
        }
        return order;
    }

}

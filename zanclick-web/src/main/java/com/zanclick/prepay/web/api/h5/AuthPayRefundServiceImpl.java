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
            PayOrder order = queryByOrderNoOrOutOrderNo(query.getOrderNo(),query.getOutOrderNo());
            Refund dto = new Refund();
            dto.setOrderNo(order.getOrderNo());
            dto.setOutRequestNo(StringUtils.getTradeNo());
            dto.setAmount(order.getAmount());
            dto.setType(0);
            dto.setReason("交易退款");
            RefundResult result = authorizePayService.refund(dto);
            if (result.isSuccess()) {
                JSONObject object = new JSONObject();
                object.put("orderNo", query.getOrderNo());
                object.put("orderStatus", "PAY_REFUND");
                param.setData(object.toJSONString());
                return param.toString();
            }
            param.setMessage(result.getMessage());
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
     * 根据订单号查询
     * @param orderNo
     * @param outOrderNo
     * @return
     * */
    private PayOrder queryByOrderNoOrOutOrderNo(String orderNo,String outOrderNo){
        PayOrder order = null;
        if (orderNo != null){
            order = payOrderService.queryByOrderNo(orderNo);
        }
        if (order == null && outOrderNo != null){
            order = payOrderService.queryByOutOrderNo(outOrderNo);
        }
        if (order == null){
            log.error("交易订单号异常:{},{}",orderNo,outOrderNo);
            throw new BizException("交易订单号异常");
        }
        //TODO 状态异常
        return order;
    }

}

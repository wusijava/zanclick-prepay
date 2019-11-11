package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.authorize.dto.PayResult;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.AuthorizePay;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.ApiPay;
import com.zanclick.prepay.web.dto.ApiPayResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * 预授权交易流水创建
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickCreateAuthPrePay")
public class AuthPrePayServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizePayService authorizePayService;

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("创建成功");
        try {
            String decrypt = verifyCipherJson(appId, cipherJson);
            ApiPay dto = parser(decrypt, ApiPay.class);
            String check = dto.check();
            if (check != null) {
                param.setFail();
                param.setMessage(check);
                return param.toString();
            }
            PayOrder order = getPayOrder(dto);
            ApiPayResult result = getPayResult(order);
            param.setData(result);
            return param.toString();
        } catch (BizException be) {
            param.setMessage(be.getMessage());
            log.error("创建订单异常:{}", be);
        } catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}", e);
        }
        param.setFail();
        return param.toString();
    }

    /**
     * 获取支付封装类
     *
     * @param pay
     * @return
     */
    private PayOrder getPayOrder(ApiPay pay) {
        PayOrder payOrder = payOrderService.queryAndHandlePayOrder(null,pay.getOutOrderNo());
        if (payOrder.isWait() && DataUtil.isEmpty(payOrder.getRequestNo())){
            PayResult result = authorizePayService.prePay(getPay(payOrder));
            if (result.isSuccess()) {
                payOrder.setRequestNo(result.getRequestNo());
                payOrder.setQrCodeUrl(result.getQrCodeUrl());
                payOrderService.handlePayOrder(payOrder);
            }else {
                throw new BizException(result.getMessage());
            }
        }
        return payOrder;
    }

    /**
     * 获取支付封装类
     *
     * @param order
     * @return
     */
    private ApiPayResult getPayResult(PayOrder order) {
        ApiPayResult payResult = new ApiPayResult();
        payResult.setStatus(getApiPayStatus(order.getState()));
        payResult.setTotalMoney(order.getAmount());
        payResult.setNum(order.getNum());
        payResult.setTitle(order.getTitle());
        payResult.setOrderNo(order.getOutTradeNo());
        payResult.setQrCodeUrl(order.getQrCodeUrl());
        payResult.setEachMoney(order.getEachMoney());
        if (DataUtil.isNotEmpty(order.getMerchantNo())){
            AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(order.getMerchantNo());
            if (DataUtil.isNotEmpty(merchant)){
                payResult.setStoreName(merchant.getStoreName());
            }
        }
        return payResult;
    }

    /**
     * 获取支付封装类
     *
     * @param order
     * @return
     */
    private AuthorizePay getPay(PayOrder order) {
        AuthorizePay dto = new AuthorizePay();
        dto.setMerchantNo(order.getMerchantNo());
        dto.setAmount(order.getAmount());
        dto.setDesc(order.getTitle());
        dto.setNum(order.getNum());
        dto.setOutTradeNo(order.getOutTradeNo());
        return dto;
    }
}

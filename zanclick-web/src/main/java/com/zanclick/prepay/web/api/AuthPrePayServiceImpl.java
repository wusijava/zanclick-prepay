package com.zanclick.prepay.web.api;

import com.zanclick.prepay.authorize.vo.AuthorizePay;
import com.zanclick.prepay.authorize.dto.PayResult;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.web.dto.ApiPay;
import com.zanclick.prepay.web.dto.ApiPayResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 预授权二维码支付
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
    private SetMealService setMealService;
    @Autowired
    private PayOrderService payOrderService;

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
            PayOrder order = getPayOrder(dto, appId);
            if (order.isPayed()){
                param.setData(getPayResult(order,null));
                return param.toString();
            }
            PayResult result = authorizePayService.prePay(getPay(order));
            if (result.isSuccess()) {
                order.setOrderNo(result.getOrderNo());
                payOrderService.handlePayOrder(order);
                param.setData(getPayResult(order,result));
                return param.toString();
            }
            order.setState(PayOrder.State.closed.getCode());
            order.setFinishTime(new Date());
            payOrderService.handlePayOrder(order);
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
     * 获取支付封装类
     *
     * @param pay
     * @return
     */
    private PayOrder getPayOrder(ApiPay pay, String appId) {
        PayOrder payOrder = payOrderService.queryByOutOrderNo(pay.getOutOrderNo());
        if (DataUtil.isNotEmpty(payOrder) && payOrder.isPayed()){
            throw new BizException("交易已支付");
        }
        if (DataUtil.isNotEmpty(payOrder) && payOrder.isWait()){
            return payOrder;
        }
        SetMeal meal = setMealService.queryByPackageNo(pay.getPackageNo());
        if (DataUtil.isEmpty(meal)) {
            throw new BizException("套餐编码错误");
        }
        if (SetMeal.State.closed.getCode().equals(meal.getState())) {
            throw new BizException("套餐已下架");
        }
        payOrder = new PayOrder();
        payOrder.setPackageNo(pay.getPackageNo());
        payOrder.setAmount(meal.getTotalAmount());
        payOrder.setAppId(appId);
        payOrder.setNum(meal.getNum());
        payOrder.setTitle(meal.getTitle());
        payOrder.setCreateTime(new Date());
        payOrder.setMerchantNo(pay.getMerchantNo());
        payOrder.setOutOrderNo(pay.getOutOrderNo());
        payOrder.setProvince(pay.getProvince());
        payOrder.setCity(pay.getCity());
        payOrder.setState(PayOrder.State.wait.getCode());
        payOrder.setPhoneNumber(pay.getPhoneNumber());
        payOrderService.insert(payOrder);
        return payOrder;
    }

    /**
     * 获取支付封装类
     *
     * @param order
     * @param result
     * @return
     */
    private ApiPayResult getPayResult(PayOrder order,PayResult result) {
        //TODO 调整
        ApiPayResult payResult = new ApiPayResult();
        payResult.setState(order.getState());
        payResult.setTotalMoney(order.getAmount());
        payResult.setNum(order.getNum());
        payResult.setTitle(order.getTitle());
        if (DataUtil.isNotEmpty(result)){
            payResult.setOrderNo(result.getOrderNo());
            payResult.setQrCodeUrl(result.getQrCodeUrl());
            payResult.setEachMoney(result.getEachMoney());
        }
        if (order.isPayed()){
            order.setOrderNo(order.getOrderNo());
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
        dto.setOutOrderNo(order.getOutOrderNo());
        return dto;
    }
}

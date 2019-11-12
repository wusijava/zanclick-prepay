package com.zanclick.prepay.web.api.h5;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.SettleRate;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.SettleRateService;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.ApiPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Date;

/**
 * 预授权订单创建
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickCreateOrder")
public class CreateOrderServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private SetMealService setMealService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private SettleRateService settleRateService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Value("${h5.server}")
    private String h5Server;

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
            JSONObject object = createPayOrder(dto, appId);
            StringBuffer sb = new StringBuffer();
            sb.append(h5Server+"/order/orderConfirmation");
            sb.append("?appId=" + appId).append("&cipherJson=" + URLEncoder.encode(cipherJson, "utf-8"));
            object.put("url",sb.toString());
            param.setData(object);
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
     * 获取支付封装类
     *
     * @param pay
     * @return
     */
    private JSONObject createPayOrder(ApiPay pay, String appId) {
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(pay.getMerchantNo());
        if (merchant == null || !merchant.isSuccess()){
            log.error("商户号异常:{}",pay.getMerchantNo());
            throw new BizException("商户号异常:"+pay.getMerchantNo());
        }
        PayOrder payOrder = payOrderService.queryByOutOrderNo(pay.getOutOrderNo());
        if (DataUtil.isNotEmpty(payOrder)){
            if (payOrder.isPayed() || payOrder.isWait()){
                return getResult(payOrder);
            }
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
        payOrder.setStoreName(merchant.getStoreName());
        payOrder.setDealState(PayOrder.DealState.notice_wait.getCode());
        payOrder.setReason("等待通知");
        payOrder.setWayId(merchant.getWayId());
        payOrder.setNum(meal.getNum());
        payOrder.setTitle(meal.getTitle());
        payOrder.setCreateTime(new Date());
        payOrder.setMerchantNo(pay.getMerchantNo());
        payOrder.setOutOrderNo(pay.getOutOrderNo());
        payOrder.setOutTradeNo(StringUtils.getTradeNo());
        payOrder.setProvince(pay.getProvince());
        payOrder.setCity(pay.getCity());
        payOrder.setState(PayOrder.State.wait.getCode());
        payOrder.setPhoneNumber(pay.getPhoneNumber());
        SettleRate rate = settleRateService.queryByAppId(payOrder.getAppId(),payOrder.getNum());
        String settleAmount = MoneyUtil.divide(payOrder.getAmount(),rate.getRate());
        payOrder.setSettleAmount(settleAmount);
        String eachAmount = MoneyUtil.divide(payOrder.getAmount(),payOrder.getNum().toString());
        String amount = MoneyUtil.multiply(eachAmount,String.valueOf(payOrder.getNum()-1));
        String firstAmount = MoneyUtil.subtract(payOrder.getAmount(),amount);
        payOrder.setEachMoney(eachAmount);
        payOrder.setFirstMoney(firstAmount);
        payOrderService.insert(payOrder);
        return getResult(payOrder);
    }


    private JSONObject getResult(PayOrder payOrder){
        JSONObject object = new JSONObject();
        object.put("num",payOrder.getNum());
        object.put("title",payOrder.getTitle());
        object.put("orderNo",payOrder.getOutTradeNo());
        object.put("totalMoney",payOrder.getAmount());
        object.put("eachMoney",payOrder.getEachMoney());
        return object;
    }
}
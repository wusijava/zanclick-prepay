package com.zanclick.prepay.web.api;

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
import com.zanclick.prepay.web.dto.ApiPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            createPayOrder(dto, appId);
            param.setMessage("创建成功");
            param.setSuccess();
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
    private void createPayOrder(ApiPay pay, String appId) {
        PayOrder payOrder = payOrderService.queryByOutOrderNo(pay.getOutOrderNo());
        if (DataUtil.isNotEmpty(payOrder) && payOrder.isPayed()){
            throw new BizException("交易已支付");
        }
        if (DataUtil.isNotEmpty(payOrder) && payOrder.isWait()){
            return;
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
        payOrder.setOutTradeNo(StringUtils.getTradeNo());
        payOrder.setProvince(pay.getProvince());
        payOrder.setCity(pay.getCity());
        payOrder.setState(PayOrder.State.wait.getCode());
        payOrder.setPhoneNumber(pay.getPhoneNumber());
        SettleRate rate = settleRateService.queryByAppId(payOrder.getAppId());
        String settleAmount = MoneyUtil.divide(payOrder.getAmount(),rate.getRate());
        payOrder.setSettleAmount(settleAmount);
        String eachAmount = MoneyUtil.divide(payOrder.getAmount(),payOrder.getNum().toString());
        String amount = MoneyUtil.multiply(eachAmount,String.valueOf(payOrder.getNum()-1));
        String firstAmount = MoneyUtil.subtract(payOrder.getAmount(),amount);
        payOrder.setEachMoney(eachAmount);
        payOrder.setFirstMoney(firstAmount);
        payOrderService.insert(payOrder);
    }
}

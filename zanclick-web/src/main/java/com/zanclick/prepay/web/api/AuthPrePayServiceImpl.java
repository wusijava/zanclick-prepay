package com.zanclick.prepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.PayDTO;
import com.zanclick.prepay.authorize.dto.PayResult;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.web.dto.ApiPay;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("创建成功");
        try {
            String decrypt = verifyCipherJson(appId, cipherJson);
            ApiPay dto = parser(decrypt, ApiPay.class);
            String check  = dto.check();
            if (check != null){
                param.setFail();
                param.setMessage(check);
                return param.toString();
            }
            PayResult result = authorizePayService.prePay(getPay(dto));
            if (result.isSuccess()) {
                JSONObject object = new JSONObject();
                object.put("orderNo", result.getTradeNo());
                object.put("qrCodeUrl", result.getQrCodeUrl());
                param.setData(object);
                return param.toString();
            }
            param.setMessage(result.getMessage());
        } catch (BizException be) {
            param.setMessage(be.getMessage());
            log.error("查询异常:{}", be);
        } catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}", 3);
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
    private PayDTO getPay(ApiPay pay) {
        SetMeal meal = setMealService.queryByPackageNo(pay.getPackageNo());
        if (DataUtil.isEmpty(meal)) {
            throw new BizException("套餐编码错误");
        }
        if (SetMeal.State.closed.getCode().equals(meal.getState())) {
            throw new BizException("套餐已下架");
        }
        PayDTO dto = new PayDTO();
        dto.setMerchantNo(pay.getMerchantNo());
        dto.setAmount(meal.getAmount());
        dto.setDesc(meal.getTitle());
        dto.setNum(meal.getNum());
        dto.setOutOrderNo(pay.getOutOrderNo());
        return dto;
    }
}

package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.dto.PayResult;
import com.zanclick.prepay.authorize.dto.api.ApiPay;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.AESUtil;
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
public class AuthPrePayServiceImpl extends AbstractCommonMethod implements ApiRequestResolver {
    
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private AppInfoService appInfoService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null || appInfo.getState().equals(AppInfo.State.close.getCode())) {
            param.setFail();
            param.setMessage("应用信息异常");
            return param.toString();
        }
        String decrypt = AESUtil.Decrypt(cipherJson, appInfo.getKey());
        if (decrypt == null) {
            param.setFail();
            param.setMessage("商户信息验证失败");
            return param.toString();
        }
        ApiPay dto = parser(request, ApiPay.class);
        String check = dto.check();
        if (check != null) {
            param.setFail();
            param.setMessage(check);
            return param.toString();
        }
        PayResult result = authorizePayService.prePay(dto);
        if (result.isSuccess()){
            JSONObject object = new JSONObject();
            object.put("orderNo",result.getTradeNo());
            object.put("qrCodeUrl",result.getQrCodeUrl());
            param.setData(object.toJSONString());
            param.setSuccess();
            param.setMessage("创建成功");
            return param.toString();
        }
        param.setFail();
        param.setMessage(result.getMessage());
        return param.toString();
    }
}

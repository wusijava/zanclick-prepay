package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权二维码支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public abstract class AbstractCommonMethod {

    @Autowired
    private AppInfoService appInfoService;

    public String verifyCipherJson(String appId,String cipherJson){
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null || appInfo.getState().equals(AppInfo.State.close.getCode())) {
            throw new BizException("应用信息异常");
        }
        String decrypt = AESUtil.Decrypt(cipherJson, appInfo.getKey());
        if (decrypt == null) {
            throw new BizException("商户信息验证失败");
        }
        return decrypt;
    }


    public String getApiPayStatus(Integer state){
        if (AuthorizeOrder.State.payed.getCode().equals(state)){
            return "PAY_SUCCESS";
        }else if (AuthorizeOrder.State.failed.getCode().equals(state) || AuthorizeOrder.State.closed.getCode().equals(state)){
            return "PAY_CLOSED";
        }else if (AuthorizeOrder.State.refund.getCode().equals(state)){
            return "PAY_REFUND";
        }else {
            return "WAIT_PAY";
        }
    }

    public <T> T parser(String content, Class<T> tClass) {
        return JSONObject.parseObject(content, tClass);
    }
}

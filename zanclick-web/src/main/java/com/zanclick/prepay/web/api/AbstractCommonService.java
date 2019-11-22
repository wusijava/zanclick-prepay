package com.zanclick.prepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.AesUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.web.exeption.DecryptException;
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
@Service
public abstract class AbstractCommonService {

    @Autowired
    private AppInfoService appInfoService;

    public void verifyAppId(String appId){
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null || appInfo.getState().equals(AppInfo.State.close.getCode())) {
            log.error("应用异常,商户提交信息:{}",appId);
            throw new BizException("应用信息异常");
        }
    }

    public String verifyCipherJson(String appId,String cipherJson){
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null || appInfo.getState().equals(AppInfo.State.close.getCode())) {
            log.error("应用异常,商户提交信息:{},{}",appId,cipherJson);
            throw new BizException("应用信息异常");
        }
        String decrypt = AesUtil.Decrypt(cipherJson, appInfo.getKey());
        if (decrypt == null) {
            log.error("解密失败,商户提交信息:{},{}",appId,cipherJson);
            throw new DecryptException("解密失败,请检查加密信息是否正确");
        }
        return decrypt;
    }


    public String getApiPayStatus(Integer state){
        if (PayOrder.State.payed.getCode().equals(state)){
            return "PAY_SUCCESS";
        }else if (PayOrder.State.closed.getCode().equals(state) || AuthorizeOrder.State.closed.getCode().equals(state)){
            return "PAY_CLOSED";
        }else if (PayOrder.State.refund.getCode().equals(state)){
            return "PAY_REFUND";
        }else {
            return "WAIT_PAY";
        }
    }

    public <T> T parser(String content, Class<T> tClass) {
        return JSONObject.parseObject(content, tClass);
    }
}

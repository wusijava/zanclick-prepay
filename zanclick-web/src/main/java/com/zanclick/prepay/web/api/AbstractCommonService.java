package com.zanclick.prepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权二维码支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public abstract class AbstractCommonService {

    @Autowired
    private AppInfoService appInfoService;

    public String verifyCipherJson(String appId,String cipherJson){
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null || appInfo.getState().equals(AppInfo.State.close.getCode())) {
            throw new BizException("应用信息异常");
        }
        String decrypt = AesUtil.Decrypt(cipherJson, appInfo.getKey());
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


    /**
     * 获取状态描述
     *
     * @param state
     * @return
     */
    public String getStateDesc(Integer state) {
        if (state.equals(-1) || state.equals(-2)) {
            return "已关闭";
        } else if (state.equals(1)) {
            return "已支付";
        } else if (state.equals(2)) {
            return "支付中";
        } else if (state.equals(3)) {
            return "结算中";
        } else if (state.equals(4)) {
            return "已结算";
        } else if (state.equals(5)) {
            return "已退款";
        } else {
            return "待支付";
        }
    }

    public <T> T parser(String content, Class<T> tClass) {
        return JSONObject.parseObject(content, tClass);
    }
}

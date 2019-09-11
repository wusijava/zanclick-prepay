package com.zanclick.prepay.authorize.api;

import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权商户
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickVerifyIdentity")
public class IdentityVerifyServiceImpl extends AbstractCommonMethod implements ApiRequestResolver {

    @Autowired
    private AppInfoService appInfoService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        AppInfo appInfo = appInfoService.queryByAppId(appId);
        if (appInfo == null) {
            param.setFail();
            param.setMessage("应用ID错误");
            return param.toString();
        }
        if (appInfo.getState().equals(AppInfo.State.close.getCode())) {
            param.setFail();
            param.setMessage("应用已被关闭");
            return param.toString();
        }
        String decrypt = AESUtil.Decrypt(cipherJson, appInfo.getKey());
        if (decrypt == null) {
            param.setFail();
            param.setMessage("商户加密信息有误");
            return param.toString();
        }
        param.setSuccess();
        param.setMessage("验证成功");
        return param.toString();
    }
}

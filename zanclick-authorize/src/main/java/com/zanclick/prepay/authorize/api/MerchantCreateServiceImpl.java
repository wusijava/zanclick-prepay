package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.dto.RegisterMerchant;
import com.zanclick.prepay.authorize.dto.api.ApiRegisterMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权商户注册
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickCreateMerchant")
public class MerchantCreateServiceImpl extends AbstractCommonMethod implements ApiRequestResolver {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

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
        ApiRegisterMerchant apiMerchant = parser(request, ApiRegisterMerchant.class);
        String check = apiMerchant.check();
        if (check != null) {
            param.setFail();
            param.setMessage(check);
            return param.toString();
        }
        String decrypt = AESUtil.Decrypt(cipherJson, appInfo.getKey());
        if (decrypt == null) {
            param.setFail();
            param.setMessage("商户信息验证失败");
            return param.toString();
        }
        JSONObject object = JSONObject.parseObject(decrypt);
        try {
            RegisterMerchant merchant = new RegisterMerchant();
            merchant.setMerchantNo(object.getString("merchantNo"));
            merchant.setStoreNo(object.getString("storeNo"));
            merchant.setStoreSubjectName(apiMerchant.getStoreSubjectName());
            merchant.setStoreSubjectCertNo(apiMerchant.getStoreSubjectCertNo());
            merchant.setStoreName(apiMerchant.getStoreName());
            merchant.setStoreProvince(apiMerchant.getStoreProvince());
            merchant.setStoreCity(apiMerchant.getStoreCity());
            merchant.setStoreCounty(apiMerchant.getStoreCounty());
            merchant.setAppId(appId);
            merchant.setContactName(apiMerchant.getContactName());
            merchant.setContactPhone(apiMerchant.getContactPhone());
            merchant.setOperatorName("中国移动");
            merchant.setName(apiMerchant.getName());
            merchant.setSellerNo(apiMerchant.getSellerNo());
            authorizeMerchantService.createMerchant(merchant);
            param.setSuccess();
            param.setMessage("签约成功");
            return param.toString();
        } catch (BizException be) {
            log.error("商户创建业务异常:{}", be);
            param.setMessage(be.getMessage());
        } catch (Exception e) {
            log.error("商户创建异常:{}", e);
            param.setMessage("系统异常，请稍后再试");
        }
        param.setFail();
        return param.toString();
    }
}

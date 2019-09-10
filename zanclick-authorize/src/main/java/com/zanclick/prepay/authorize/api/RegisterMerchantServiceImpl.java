package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.RegisterMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.entity.RequestParam;
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
@Service("comAuthMerchantRegister")
public class RegisterMerchantServiceImpl extends AbstractUtil implements ApiRequestResolver {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Override
    public String resolve(String request) {
        ResponseParam param = new ResponseParam();
        RegisterMerchant merchant = parser(request,RegisterMerchant.class);
        String check = merchant.check();
        if (check != null){
            param.setFail();
            param.setMessage(check);
            return param.toString();
        }
        try {
            authorizeMerchantService.createMerchant(merchant);
            param.setSuccess();
            param.setMessage("签约成功");
        }catch (BizException be){
            log.error("商户创建业务异常:{}",be);
            param.setMessage(be.getMessage());
        }catch (Exception e){
            log.error("商户创建异常:{}",e);
            param.setMessage("系统异常，请稍后再试");
        }
        param.setFail();
        param.setMessage("签约成功");
        return param.toString();
    }

}

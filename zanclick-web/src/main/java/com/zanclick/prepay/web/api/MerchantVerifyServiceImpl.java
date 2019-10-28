package com.zanclick.prepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.web.dto.ApiVerifyMerchant;
import com.zanclick.prepay.web.dto.VerifyMerchant;
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
@Service("comZanclickVerifyMerchant")
public class MerchantVerifyServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("验证成功");
        try {
            ApiVerifyMerchant apiVerifyMerchant = JSONObject.parseObject(verifyCipherJson(appId,cipherJson),ApiVerifyMerchant.class);
            String check = apiVerifyMerchant.check();
            if (check != null) {
                param.setFail();
                param.setMessage(check);
                return param.toString();
            }
            AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(apiVerifyMerchant.getMerchantNo());
            if (merchant == null) {
                param.setFail();
                param.setMessage("商户未签约");
                return param.toString();
            }
            if (merchant.getWayId() == null || merchant.getWayId().equals(apiVerifyMerchant.getWayid())){
                param.setFail();
                param.setMessage("渠道编号与商户号对应有误");
                return param.toString();
            }
            VerifyMerchant verifyMerchant = new VerifyMerchant();
            verifyMerchant.setManagerMobile(merchant.getContactPhone());
            verifyMerchant.setManagerName(merchant.getContactName());
            verifyMerchant.setMerchantName(merchant.getStoreSubjectName());
            verifyMerchant.setMerchantNo(merchant.getMerchantNo());
            verifyMerchant.setRegisterStatus(getRegisterStatus(merchant.getState()));
            verifyMerchant.setWayid(merchant.getWayId());
            param.setData(verifyMerchant);
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

    private Integer getRegisterStatus(Integer state){
        if (AuthorizeMerchant.State.success.getCode().equals(state)){
            return 1;
        }else {
            return 0;
        }
    }
}

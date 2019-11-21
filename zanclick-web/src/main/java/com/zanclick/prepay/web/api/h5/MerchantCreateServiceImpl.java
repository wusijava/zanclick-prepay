package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.user.query.UserQuery;
import com.zanclick.prepay.user.service.UserService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.ApiRegisterMerchant;
import com.zanclick.prepay.web.dto.ApiRegisterMerchantResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商户信息签约
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickCreateMerchant")
public class MerchantCreateServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private UserService userService;


    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("签约成功");
        ApiRegisterMerchant apiMerchant = parser(request, ApiRegisterMerchant.class);
        String check = apiMerchant.check();
        if (check != null) {
            param.setFail();
            param.setMessage(check);
            return param.toString();
        }
        try {
            verifyAppId(appId);
            apiMerchant.setAppId(appId);
            RegisterMerchant merchant = createRegisterMerchant(apiMerchant);
            AuthorizeMerchant authorizeMerchant = authorizeMerchantService.createMerchant(merchant);
            UserQuery user = userService.createUser(merchant.getSellerNo(), merchant.getStoreSubjectName(), merchant.getStoreName(), merchant.getWayId(), merchant.getContactPhone());
            authorizeMerchant.setStoreMarkCode(user.getStoreMarkCode());
            authorizeMerchant.setUid(user.getUid());
            authorizeMerchantService.updateById(authorizeMerchant);
            ApiRegisterMerchantResult result = new ApiRegisterMerchantResult();
            result.setMerchantNo(authorizeMerchant.getMerchantNo());
            result.setPassword(user.getPwd());
            param.setData(merchant.getMerchantNo());
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


    /**
     * 创建注册商户
     *
     * @param apiMerchant
     */
    private RegisterMerchant createRegisterMerchant(ApiRegisterMerchant apiMerchant) {
        RegisterMerchant merchant = new RegisterMerchant();
        merchant.setWayId(apiMerchant.getWayId());
        merchant.setMerchantNo(apiMerchant.getMerchantNo());
        merchant.setStoreNo(apiMerchant.getStoreNo());
        merchant.setStoreSubjectName(apiMerchant.getStoreSubjectName());
        merchant.setStoreSubjectCertNo(apiMerchant.getStoreSubjectCertNo());
        merchant.setStoreName(apiMerchant.getStoreName());
        merchant.setStoreProvince(apiMerchant.getStoreProvince());
        merchant.setStoreCity(apiMerchant.getStoreCity());
        merchant.setStoreCounty(apiMerchant.getStoreCounty());
        merchant.setAppId(apiMerchant.getAppId());
        merchant.setContactName(apiMerchant.getContactName());
        merchant.setContactPhone(apiMerchant.getContactPhone());
        merchant.setOperatorName("中国移动");
        merchant.setName(apiMerchant.getName());
        merchant.setSellerNo(apiMerchant.getSellerNo());
        return merchant;
    }
}

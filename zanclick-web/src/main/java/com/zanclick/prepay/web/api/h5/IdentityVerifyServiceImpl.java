package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.web.api.AbstractCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 身份信息验证
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickVerifyIdentity")
public class IdentityVerifyServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("验证成功");
        try {
            verifyCipherJson(appId,cipherJson);
            return param.toString();
        }catch (BizException be){
            param.setMessage(be.getMessage());
            log.error("查询异常:{}",be);
        }catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}",e);
        }
        return param.toString();
    }
}

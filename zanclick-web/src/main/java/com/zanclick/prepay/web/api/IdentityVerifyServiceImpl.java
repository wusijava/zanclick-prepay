package com.zanclick.prepay.web.api;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import lombok.extern.slf4j.Slf4j;
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
            log.error("系统异常:{}",3);
        }
        return param.toString();
    }
}

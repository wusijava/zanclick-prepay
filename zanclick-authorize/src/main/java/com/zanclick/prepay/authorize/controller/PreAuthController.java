package com.zanclick.prepay.authorize.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.event.BaseEventResolver;
import com.zanclick.prepay.authorize.event.RespInfo;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.DataUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lvlu
 * @date 2019-05-07 17:40
 **/
@Api(description = "网商垫资网关")
@Controller
@RequestMapping
@Slf4j
public class PreAuthController extends BaseController {

    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @ApiOperation(value = "网商垫资网关")
    @PostMapping(value = "/preAuth")
    @ResponseBody
    public RespInfo gateway(HttpServletRequest request){
        Map<String,String> params = getAllRequestParam(request);
        log.error("收到网商垫资异步结果消息:{}",JSON.toJSONString(params));
        String key = params.get("key");
        String dataObject = params.get("dataobject");
        String sign = params.get("sign");
        String isvAppId = params.get("app_id");
        if(DataUtil.isEmpty(key) || DataUtil.isEmpty(dataObject)
                || DataUtil.isEmpty(sign) || DataUtil.isEmpty(isvAppId)){
            log.error("请求参数不完整");
            return RespInfo.fail("请求参数不完整");
        }
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryByIsvAppId(isvAppId);
        if (DataUtil.isEmpty(configuration)){
            log.error("appId有误:{}",isvAppId);
            return RespInfo.fail("appId有误");
        }
        boolean verify_result;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params,
                    configuration.getMyBankPublicKey(),
                    configuration.getCharset(), configuration.getSignType());
        }catch (Exception e) {
            log.error("异步通知接收失败："+e);
            return RespInfo.fail("验签失败");
        }
        if(!verify_result){
            log.error("验签失败");
            return RespInfo.fail("验签失败");
        }else{
            log.error("验签成功");
        }
        try {
            String serviceName = key + "Resolver";
            BaseEventResolver eventResolver = ApplicationContextProvider.getBean(serviceName,BaseEventResolver.class);
            eventResolver.resolveEvent(dataObject);
            return RespInfo.success();
        }catch (Exception e){
            e.printStackTrace();
            return RespInfo.fail("未知错误");
        }
    }
}

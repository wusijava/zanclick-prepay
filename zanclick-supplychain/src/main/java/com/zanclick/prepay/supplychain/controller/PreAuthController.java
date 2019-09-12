package com.zanclick.prepay.supplychain.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.supplychain.config.SupplyChainConfig;
import com.zanclick.prepay.supplychain.event.BaseEventResolver;
import com.zanclick.prepay.supplychain.event.RespInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lvlu
 * @date 2019-05-07 17:40
 **/
@Controller
@RequestMapping
@Slf4j
public class PreAuthController extends BaseController {

    @RequestMapping(value = "/preAuth")
    @ResponseBody
    public RespInfo gateway(HttpServletRequest request){
        Map<String,String> params = getAllRequestParam(request);
        log.error("收到网商垫资异步结果消息:{}",JSON.toJSONString(params));
        String key = params.get("key");
        String dataobject = params.get("dataobject");
        String charset = params.get("charset");
        String sign = params.get("sign");
        String signType = params.get("sign_type");
        if(DataUtil.isEmpty(key) || DataUtil.isEmpty(dataobject)
                || DataUtil.isEmpty(sign) || DataUtil.isEmpty(charset) ){
            log.error("请求参数不完整");
            return RespInfo.fail("请求参数不完整");
        }
        boolean verify_result;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params,
                    SupplyChainConfig.MYBANK_PUBLIC_KEY,
                    charset, signType);
        }catch (Exception e) {
            log.warn("异步通知接收失败："+e);
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
            eventResolver.resolveEvent(dataobject);
            return RespInfo.success();
        }catch (Exception e){
            e.printStackTrace();
            return RespInfo.fail("未知错误");
        }
    }
}

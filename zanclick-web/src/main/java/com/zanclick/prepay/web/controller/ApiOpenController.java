package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 中心网关
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "api_open_controller")
public class ApiOpenController {

    @GetMapping(value = "/verifyMerchant",produces = "application/json;charset=utf-8")
    public Response verifyMerchant(String appId, String cipherJson) {
        String method = "com.zanclick.verify.merchant";
        String methodName = StringUtils.getMethodName(method);
        ResponseParam param = resolver(methodName,appId,cipherJson);
        if (param.isSuccess()){
            return Response.ok(param.getData());
        }
        return Response.fail(param.getMessage());
    }

    @GetMapping(value = "/createQr")
    public Response createQr(String appId, String cipherJson, HttpServletResponse response) {
        String method = "com.zanclick.create.auth.prePay";
        String methodName = StringUtils.getMethodName(method);
        try {
            ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(methodName);
            String result = resolver.resolve(appId,cipherJson,null);
            ResponseParam param = JSONObject.parseObject(result,ResponseParam.class);
            if (!param.isSuccess()){
                return Response.fail(param.getMessage());
            }
            response.sendRedirect("https://www.baidu.com");
        }catch (Exception e){
            log.error("系统异常:{}",e);
            return Response.fail("系统繁忙，请稍后再试");
        }
        return Response.ok("");
    }


    @GetMapping(value = "/queryOrderList",produces = "application/json;charset=utf-8")
    public Response queryOrderList(String appId, String cipherJson) {
        String method = "com.zanclick.verify.merchant";
        String methodName = StringUtils.getMethodName(method);
        ResponseParam param = resolver(methodName,appId,cipherJson);
        if (param.isSuccess()){
            return Response.ok(param.getData());
        }
        return Response.fail(param.getMessage());
    }

    private ResponseParam resolver(String methodName,String appId,String cipherJson){
        try {
            ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(methodName);
            String result = resolver.resolve(appId,cipherJson,null);
            ResponseParam param = JSONObject.parseObject(result,ResponseParam.class);
            return param;
        }catch (Exception e){
            log.error("系统异常:{}",e);
            ResponseParam param = new ResponseParam();
            param.setFail();
            param.setMessage("系统繁忙,请稍后再试");
            return param;
        }
    }
}

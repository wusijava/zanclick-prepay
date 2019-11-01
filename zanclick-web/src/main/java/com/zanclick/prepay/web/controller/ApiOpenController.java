package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.web.exeption.DecryptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 中心网关
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "api_open_controller")
public class ApiOpenController {

    @Value("${h5.server}")
    private String h5Server;

    @GetMapping(value = "/verifyMerchant", produces = "application/json;charset=utf-8")
    public Response verifyMerchant(String appId, String cipherJson) {
        String method = "com.zanclick.verify.merchant";
        try {
            ResponseParam param = resolver(method, appId, cipherJson);
            if (param.isSuccess()) {
                return Response.ok(param.getData());
            }
            return Response.unsigned(param.getMessage());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        }
    }

    @GetMapping(value = "/createQr")
    public void createQr(String appId, String cipherJson, HttpServletResponse response) {
        String method = "com.zanclick.create.order";
        String message = null;
        StringBuffer sb = new StringBuffer();
        try {
            ResponseParam param = resolver(method,appId, cipherJson);
            if (!param.isSuccess()) {
                sb.append("/auth/fail");
                sb.append("?desc="+URLEncoder.encode(param.getMessage(), "utf-8"));
                response.sendRedirect(h5Server + sb.toString());
                return;
            }else {
                sb.append("/trade/create");
                sb.append("?appId=" + appId).append("&cipherJson=" + URLEncoder.encode(cipherJson, "utf-8"));
                response.sendRedirect(h5Server + sb.toString());
                return;
            }
        } catch (DecryptException e) {
            log.error("订单创建,解密失败:{}", e);
            message = "解密失败，请检查加密信息";
        } catch (Exception e) {
            log.error("订单创建,系统异常:{}", e);
            message = "系统繁忙，请稍后再试";
        }
        try {
            sb.append("/auth/fail");
            sb.append("?desc="+URLEncoder.encode(message, "utf-8"));
            response.sendRedirect(h5Server + sb.toString());
        } catch (IOException e) {
            log.error("重定向失败:{}", e);
        }
    }

    @GetMapping(value = "/queryOrderList", produces = "application/json;charset=utf-8")
    public Response queryOrderList(String appId, String cipherJson) {
        try {
            String method = "com.zanclick.query.auth.order";
            ResponseParam param = resolver(method, appId, cipherJson);
            if (param.isSuccess()) {
                return Response.ok(param.getData());
            }
            return Response.fail(param.getMessage());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        }
    }

    private ResponseParam resolver(String method, String appId, String cipherJson) {
        try {
            String methodName = StringUtils.getMethodName(method);
            ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(methodName);
            String result = resolver.resolve(appId, cipherJson, null);
            ResponseParam param = JSONObject.parseObject(result, ResponseParam.class);
            return param;
        } catch (DecryptException e) {
            throw e;
        } catch (Exception e) {
            log.error("系统异常:{}", e);
            ResponseParam param = new ResponseParam();
            param.setFail();
            param.setMessage("系统繁忙,请稍后再试");
            return param;
        }
    }

}

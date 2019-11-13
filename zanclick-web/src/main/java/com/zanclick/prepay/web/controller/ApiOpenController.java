package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.web.exeption.DecryptException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 第三方放get请求调用接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@Api(description = "第三方放get请求调用接口")
@RestController(value = "api_open_controller")
public class ApiOpenController {

    @Value("${h5.server}")
    private String h5Server;

    @ApiOperation(value = "商户信息验证接口")
    @GetMapping(value = "/verifyMerchant", produces = "application/json;charset=utf-8")
    public Response verifyMerchant(String appId, String cipherJson) {
        String method = "com.zanclick.verify.merchant";
        return common(appId, cipherJson, method);
    }

    @ApiOperation(value = "二维码创建接口")
    @GetMapping(value = "/createQr")
    public void createQr(String appId, String cipherJson, HttpServletResponse response) throws IOException {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("/trade/create");
            sb.append("?appId=" + appId).append("&cipherJson=" + URLEncoder.encode(cipherJson, "utf-8"));
            response.sendRedirect(h5Server + sb.toString());
        } catch (Exception e) {
            log.error("订单创建,系统异常:{},{},{}", appId, cipherJson, e);
            sb.append("/auth/fail");
            sb.append("?desc=" + URLEncoder.encode("订单创建异常，请稍后再试", "utf-8"));
            response.sendRedirect(h5Server + sb.toString());
        }
    }

    @ApiOperation(value = "订单信息查询接口")
    @GetMapping(value = "/queryOrderList", produces = "application/json;charset=utf-8")
    public Response queryOrderList(String appId, String cipherJson) {
        String method = "com.zanclick.query.auth.order";
        return common(appId, cipherJson, method);
    }

    @ApiOperation(value = "退款接口")
    @GetMapping(value = "/refund", produces = "application/json;charset=utf-8")
    public Response refund(String appId, String cipherJson) {
        String method = "com.zanclick.refund.authPay";
        return common(appId, cipherJson, method);
    }

    /**
     * 公共的方法调用
     *
     * @param appId
     * @param cipherJson
     * @param method
     */
    private Response common(String appId, String cipherJson, String method) {
        try {
            ResponseParam param = resolver(method, appId, cipherJson);
            if (param.isSuccess()) {
                return Response.ok(param.getData());
            }
            return Response.fail(param.getMessage());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        }
    }

    /**
     * 公共的方法调用
     *
     * @param appId
     * @param cipherJson
     * @param method
     */
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

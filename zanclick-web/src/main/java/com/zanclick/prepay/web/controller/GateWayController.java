package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.AESUtil;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.web.dto.RequestContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中心网关
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "H5页面请求网关")
public class GateWayController {

    @PostMapping(value = "/gateway.do",produces = "application/json;charset=utf-8")
    public String param(@RequestBody String encrypt) {
        String decrypt = AESUtil.Decrypt(encrypt);
        if (decrypt == null){
            ResponseParam param = new ResponseParam();
            param.setFail();
            param.setMessage("解密失败");
            return param.toString();
        }
        RequestContent content = JSONObject.parseObject(decrypt,RequestContent.class);
        String method = StringUtils.getMethodName(content.getMethod());
        ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(method);
        String result = resolver.resolve(content.getAppId(),content.getCipherJson(),content.getContent());
        return AESUtil.Encrypt(result);
    }

}

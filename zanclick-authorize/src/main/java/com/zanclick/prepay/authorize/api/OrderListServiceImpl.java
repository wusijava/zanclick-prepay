package com.zanclick.prepay.authorize.api;

import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预授权二维码支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanClickQueryAuthOrderList")
public class OrderListServiceImpl extends AbstractUtil implements ApiRequestResolver {

    @Autowired
    private AuthorizePayService authorizePayService;

    @Override
    public String resolve(String request) {
        ResponseParam param = new ResponseParam();
        return param.toString();
    }

}

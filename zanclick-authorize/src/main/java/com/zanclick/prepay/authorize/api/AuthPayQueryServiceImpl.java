package com.zanclick.prepay.authorize.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.app.service.AppInfoService;
import com.zanclick.prepay.authorize.dto.PayResult;
import com.zanclick.prepay.authorize.dto.QueryDTO;
import com.zanclick.prepay.authorize.dto.QueryResult;
import com.zanclick.prepay.authorize.dto.api.ApiPay;
import com.zanclick.prepay.authorize.dto.api.ApiPayQuery;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.AESUtil;
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
@Service("comZanclickQueryAuthPay")
public class AuthPayQueryServiceImpl extends AbstractCommonMethod implements ApiRequestResolver {
    @Autowired
    private AuthorizePayService authorizePayService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            verifyCipherJson(appId,cipherJson);
            ApiPayQuery query = parser(request,ApiPayQuery.class);
            QueryDTO dto = new QueryDTO();
            dto.setTradeNo(query.getOrderNo());
            QueryResult result = authorizePayService.query(dto);
            if (result.isSuccess()){
                JSONObject object = new JSONObject();
                object.put("orderNo",query.getOrderNo());
                object.put("orderStatus",getApiPayStatus(result.getState()));
                param.setData(object.toJSONString());
                return param.toString();
            }
            param.setMessage(result.getMessage());
        }catch (BizException be){
            param.setMessage(be.getMessage());
            log.error("查询异常:{}",be);
        }catch (Exception e){
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}",e);
        }
        param.setFail();
        return param.toString();
    }
}

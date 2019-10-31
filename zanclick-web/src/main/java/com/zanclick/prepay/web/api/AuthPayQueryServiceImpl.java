package com.zanclick.prepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.vo.QueryDTO;
import com.zanclick.prepay.authorize.vo.QueryResult;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.dto.ApiPayQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 这里也是直接查询的底层
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickQueryAuthPay")
public class AuthPayQueryServiceImpl extends AbstractCommonService implements ApiRequestResolver {
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private PayOrderService payOrderService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            verifyCipherJson(appId,cipherJson);
            ApiPayQuery query = parser(request,ApiPayQuery.class);
            QueryDTO dto = new QueryDTO();
            dto.setOrderNo(query.getOrderNo());
            PayOrder order = payOrderService.queryByOrderNo(query.getOrderNo());
            if (DataUtil.isEmpty(order)){
                param.setMessage("交易订号异常");
                param.setFail();
                return param.toString();
            }
            QueryResult result = authorizePayService.query(dto);
            if (result.isSuccess()){
                JSONObject object = new JSONObject();
                object.put("orderNo",query.getOrderNo());
                object.put("title",order.getTitle());
                object.put("money",order.getAmount());
                object.put("orderStatus",getApiPayStatus(result.getState()));
                param.setData(object);
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

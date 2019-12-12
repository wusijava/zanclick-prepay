package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.QueryMerchantState;
import com.zanclick.prepay.web.dto.QueryMerchantStateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("comZanclickQueryMerchantState")
public class QueryMerchantStateServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            QueryMerchantState query = parser(request, QueryMerchantState.class);
            String check = query.check();
            if (check != null) {
                param.setMessage(check);
                param.setFail();
                return param.toString();
            }
            QueryMerchantStateResult result = queryMerchant(query);
            param.setData(result);
            return param.toString();
        } catch (BizException be) {
            log.error("查询入驻状态异常:{}", be);
            param.setMessage(be.getMessage());
        } catch (Exception e) {
            log.error("查询入驻状态异常:{}", e);
            param.setMessage("系统异常，请稍后再试");
        }
        param.setFail();
        return param.toString();
    }

    private QueryMerchantStateResult queryMerchant(QueryMerchantState query) {
        QueryMerchantStateResult result = new QueryMerchantStateResult();
        AuthorizeMerchant merchant = authorizeMerchantService.queryLastOneByWayId(query.getWayId());
        if (DataUtil.isEmpty(merchant)) {
            result.setTitle("该商户暂未注册");
        } else {
            if (merchant.isSuccess()) {
                result.setTitle("该商户已注册成功");
            } else {
                result.setTitle("该商户注册失败原因如下");
                result.setMsg(merchant.getReason());
            }
        }
        return result;
    }

}

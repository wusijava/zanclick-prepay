package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.QueryOrder;
import com.zanclick.prepay.web.dto.QueryOrderStateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * 查询并修改当前订单状态
 *
 * @author duchong
 * @date 2019-11-1 17:36:29
 **/
@Slf4j
@Service("comZanclickQueryOrderState")
public class QueryOrderStateServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private PayOrderService payOrderService;

    @Value("${h5.server}")
    private String h5Server;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            String decrypt = verifyCipherJson(appId, cipherJson);
            QueryOrder query = parser(decrypt, QueryOrder.class);
            String check = query.check();
            if (check != null) {
                param.setMessage(check);
                param.setFail();
                return param.toString();
            }
            QueryOrderStateResult result = queryOrder(query);
            StringBuffer sb = new StringBuffer();
            sb.append(h5Server+"/red/packet/receive");
            sb.append("?appId=" + appId);
            sb.append("&outOrderNo=" + query.getOutOrderNo());

            StringBuffer sb1 = new StringBuffer();
            sb1.append(h5Server+"/h5/auth/success");
            sb1.append("?appId=" + appId).append("&cipherJson=" + URLEncoder.encode(cipherJson, "utf-8"));
            result.setUrl(sb.toString());
            result.setSuccessUrl(sb1.toString());
            param.setData(result);
            return param.toString();
        } catch (BizException be) {
            log.error("查询订单:{}", be);
            param.setMessage(be.getMessage());
        } catch (Exception e) {
            log.error("查询订单:{}", e);
            param.setMessage("系统异常，请稍后再试");
        }
        param.setFail();
        return param.toString();
    }

    /**
     * 查询交易详情
     *
     * @param queryOrder
     * @return
     */
    private QueryOrderStateResult queryOrder(QueryOrder queryOrder) {
        QueryOrderStateResult result = new QueryOrderStateResult();
        PayOrder order = payOrderService.queryAndHandlePayOrder(queryOrder.getOrderNo(),queryOrder.getOutOrderNo());
        result.setOrderNo(order.getOutTradeNo());
        result.setOrderStatus(getApiPayStatus(order.getState()));
        result.setOutOrderNo(order.getOutOrderNo());
        result.setTitle(order.getTitle());
        result.setTotalMoney(order.getAmount());
        return result;
    }

}

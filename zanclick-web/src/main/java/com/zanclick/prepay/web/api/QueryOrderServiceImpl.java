package com.zanclick.prepay.web.api;

import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.web.dto.QueryOrder;
import com.zanclick.prepay.web.dto.QueryOrderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * 预授权二维码支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickQueryAuthOrder")
public class QueryOrderServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeOrderService authorizeOrderService;

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
            QueryOrderResult result = queryOrder(query);
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
     * 获取交易列表展示数据
     *
     * @param orderList
     * @return
     */
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    private QueryOrderResult queryOrder(QueryOrder queryOrder) {
        QueryOrderResult result = new QueryOrderResult();
        AuthorizeOrder order = null;
        if (DataUtil.isNotEmpty(queryOrder.getOrderNo())) {
            order = authorizeOrderService.queryByOrderNo(queryOrder.getOrderNo());
            if (DataUtil.isEmpty(order)) {
                order = authorizeOrderService.queryByOutTradeNo(queryOrder.getOutOrderNo());
            }
        }
        if (order == null) {
            throw new BizException("交易订单号有误");
        }
        return result;
    }
}

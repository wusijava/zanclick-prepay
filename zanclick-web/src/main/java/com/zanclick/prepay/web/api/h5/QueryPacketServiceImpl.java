package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.QueryRedPacket;
import com.zanclick.prepay.web.dto.QueryRedPacketResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询红包领取信息
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickQueryPacket")
public class QueryPacketServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private PayOrderService payOrderService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            QueryRedPacket query = parser(request, QueryRedPacket.class);
            PayOrder order = payOrderService.queryRedPacketOrder(query.getOutOrderNo());
            param.setData(getQueryResult(order));
            return param.toString();
        } catch (BizException be) {
            param.setMessage(be.getMessage());
            log.error("查询异常:{}", be);
        } catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}", e);
        }
        param.setFail();
        return param.toString();
    }


    /**
     * 退款订单相关
     *
     * @param order
     */
    private QueryRedPacketResult getQueryResult(PayOrder order) {
        QueryRedPacketResult result = new QueryRedPacketResult();
        result.setAmount(order.getRedPackAmount());
        result.setOutOrderNo(order.getOutOrderNo());
        return result;
    }
}

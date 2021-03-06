package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.QueryOrder;
import com.zanclick.prepay.web.dto.QueryOrderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * 查询并修改当前订单状态
 *
 * @author duchong
 * @date 2019-11-1 17:36:29
 **/
@Slf4j
@Service("comZanclickQueryAuthOrder")
public class QueryOrderServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private PayOrderService payOrderService;

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
     * 查询交易详情
     *
     * @param queryOrder
     * @return
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
    private QueryOrderResult queryOrder(QueryOrder queryOrder) {
        QueryOrderResult result = new QueryOrderResult();
        PayOrder order = null;
        if (DataUtil.isNotEmpty(queryOrder.getOrderNo())){
            order = payOrderService.queryByOutTradeNo(queryOrder.getOrderNo());
        }
        if (DataUtil.isNotEmpty(queryOrder.getOutOrderNo()) && DataUtil.isEmpty(order)){
            order = payOrderService.queryByOutOrderNo(queryOrder.getOutOrderNo());
        }
        if (DataUtil.isEmpty(order)){
            log.error("订单信息异常:{},{}", queryOrder.getOrderNo(), queryOrder.getOutOrderNo());
            throw new BizException("订单信息异常");
        }
        result.setMerchantNo(order.getMerchantNo());
        result.setOrderFee(order.getAmount());
        result.setOrderNo(order.getOutTradeNo());
        result.setOrderStatus(getApiPayStatus(order.getState()));
        result.setOrderTime(sdf.format(order.getCreateTime()));
        result.setPackageNo(order.getPackageNo());
        result.setOutOrderNo(order.getOutOrderNo());
        result.setPhoneNumber(order.getPhoneNumber());
        if (DataUtil.isNotEmpty(order.getFinishTime())) {
            result.setPayTime(sdf.format(order.getFinishTime()));
        }
        return result;
    }


}

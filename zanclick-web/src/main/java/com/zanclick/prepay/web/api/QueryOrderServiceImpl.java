package com.zanclick.prepay.web.api;

import com.zanclick.prepay.authorize.vo.QueryDTO;
import com.zanclick.prepay.authorize.vo.QueryResult;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.dto.QueryOrder;
import com.zanclick.prepay.web.dto.QueryOrderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizePayService authorizePayService;

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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private QueryOrderResult queryOrder(QueryOrder queryOrder) {
        QueryOrderResult result = new QueryOrderResult();
        PayOrder order = null;
        if (DataUtil.isNotEmpty(queryOrder.getOrderNo())) {
            order = payOrderService.queryByOrderNo(queryOrder.getOrderNo());
            if (DataUtil.isEmpty(order)) {
                order = payOrderService.queryByOutOrderNo(queryOrder.getOutOrderNo());
            }
        }
        if (order == null) {
            throw new BizException("交易订单号有误");
        }
        if (order.isWait()){
            QueryDTO dto = new QueryDTO();
            dto.setOutTradeNo(order.getOrderNo());
            QueryResult queryResult = authorizePayService.query(dto);
            if (queryResult.isSuccess() && !AuthorizeOrder.State.unPay.getCode().equals(queryResult.getState())){
                if (AuthorizeOrder.State.payed.getCode().equals(queryResult.getState())){
                    order.setState(PayOrder.State.payed.getCode());
                    order.setFinishTime(new Date());
                    payOrderService.updateById(order);
                }else if (AuthorizeOrder.State.failed.getCode().equals(queryResult.getState()) || AuthorizeOrder.State.closed.getCode().equals(queryResult.getState())){
                    order.setState(PayOrder.State.closed.getCode());
                    order.setFinishTime(new Date());
                    payOrderService.updateById(order);
                }
            }
        }
        result.setMerchantNo(order.getMerchantNo());
        result.setOrderFee(order.getAmount());
        result.setOrderNo(order.getOrderNo());
        result.setOrderStatus(getH5PayStatus(order.getState()));
        result.setOrderTime(sdf.format(order.getCreateTime()));
        result.setPackageNo(order.getPackageNo());
        result.setOutOrderNo(order.getOutOrderNo());
        result.setPhoneNumber(order.getPhoneNumber());
        if (DataUtil.isNotEmpty(order.getFinishTime())) {
            result.setPayTime(sdf.format(order.getFinishTime()));
        }
        return result;
    }


    public String getH5PayStatus(Integer state) {
        if (PayOrder.State.payed.getCode().equals(state)) {
            return "TRADE_SUCCESS";
        } else if (PayOrder.State.closed.getCode().equals(state) || AuthorizeOrder.State.closed.getCode().equals(state)) {
            return "TRADE_CLOSED";
        }
        return "WAIT_PAY";
    }

}

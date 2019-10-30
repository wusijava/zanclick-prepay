package com.zanclick.prepay.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.mapper.PayOrderMapper;
import com.zanclick.prepay.order.service.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
@Slf4j
@Service
public class PayOrderServiceImpl extends BaseMybatisServiceImpl<PayOrder, Long> implements PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Override
    protected BaseMapper<PayOrder, Long> getBaseMapper() {
        return payOrderMapper;
    }

    @Override
    public PayOrder queryByOrderNo(String orderNo) {
        return payOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PayOrder queryByOutOrderNo(String outOrderNo) {
        return payOrderMapper.selectByOutOrderNo(outOrderNo);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handlePayOrder(PayOrder order) {
        if (order.isPayed()){
            sendMessage(order);
        }
        this.updateById(order);
    }

    @Override
    public void handleSuccess(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null){
            log.error("交易订单异常:{}",orderNo);
            return;
        }
        order.setState(PayOrder.State.payed.getCode());
        order.setFinishTime(new Date());
        handlePayOrder(order);
    }

    @Override
    public JSONObject sendMessage(PayOrder order) {
        String result = "";
        AsiaInfoHeader header = AsiaInfoUtil.getHeader(order.getPhoneNumber());
        try {
            JSONObject object = new JSONObject();
            object.put("orderNo", order.getOrderNo());
            object.put("outOrderNo", order.getOutOrderNo());
            object.put("packageNo", order.getPackageNo());
            object.put("payTime", sdf.format(order.getFinishTime()));
            object.put("merchantNo", order.getMerchantNo());
            object.put("orderStatus", getOrderStatus(order.getState()));
            result = RestHttpClient.post(header, object.toJSONString(), "commodity/freezenotify/v1.1.1");
            log.error("能力回调结果：{}", result);
        } catch (Exception e) {
            log.error("能力回调出错:{}",e);
            e.printStackTrace();
        }
        return JSONObject.parseObject(result);
    }

    private String getOrderStatus(Integer state){
        if (PayOrder.State.wait.getCode().equals(state)){
            return "WAIT_PAY";
        }else if (PayOrder.State.payed.getCode().equals(state)){
            return "TRADE_SUCCESS";
        }
        return "TRADE_CLOSED";
    }
}

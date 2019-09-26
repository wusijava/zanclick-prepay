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
        if (order.isPayed()) {
            AsiaInfoHeader header = AsiaInfoUtil.dev(order.getPhoneNumber());
            try {
                JSONObject object = new JSONObject();
                object.put("orderNo", order.getOrderNo());
                object.put("outOrderNo", order.getOutOrderNo());
                object.put("packageNo", order.getPackageNo());
                object.put("payTime", sdf.format(order.getFinishTime()));
                object.put("merchantNo", order.getMerchantNo());
                object.put("orderStatus", order.getState());
                String result = RestHttpClient.post(header, object.toJSONString(), "commodity/freezenotify/v1.1.1");
                log.error("能力回调结果：{}", result);
            } catch (Exception e) {
                log.error("能力回调出错:{}",e);
                e.printStackTrace();
            }
        }
        this.updateById(order);
    }
}

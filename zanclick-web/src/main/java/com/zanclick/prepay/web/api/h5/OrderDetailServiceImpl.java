package com.zanclick.prepay.web.api.h5;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.common.entity.KeyValue;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 直接查的底层订单，需要调整
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickQueryAuthOrderDetail")
public class OrderDetailServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeOrderService authorizeOrderService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            JSONObject object = JSONObject.parseObject(request);
            String requestNo = object.getString("orderNo");
            AuthorizeOrder order = authorizeOrderService.queryByRequestNo(requestNo);
            if (DataUtil.isNotEmpty(order)) {
                OrderDetail detail = getOrderDetail(order);
                param.setData(detail);
                return param.toString();
            }
            param.setMessage("交易信息异常");
        } catch (BizException be) {
            log.error("查询订单列表异常:{}", be);
            param.setMessage(be.getMessage());
        } catch (Exception e) {
            log.error("查询订单列表异常:{}", e);
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private OrderDetail getOrderDetail(AuthorizeOrder order) {
        OrderDetail detail = new OrderDetail();
        detail.setId(order.getId());
        detail.setAmount(order.getMoney());
        detail.setState(order.getState());
        detail.setStateDesc(getStateDesc(order.getState()));
        detail.setOrderNo(order.getRequestNo());
        if (order.getState().equals(1)){
            detail.setRefund(1);
        }else {
            detail.setRefund(0);
        }
        List<KeyValue> valueList = new ArrayList<>();
        valueList.add(new KeyValue("交易时间", sdf.format(order.getCreateTime())));
        valueList.add(new KeyValue("交易单号", order.getRequestNo()));
        detail.setList(valueList);
        return detail;
    }


}

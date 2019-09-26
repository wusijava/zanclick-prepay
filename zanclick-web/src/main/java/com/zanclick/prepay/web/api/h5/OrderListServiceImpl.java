package com.zanclick.prepay.web.api.h5;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.query.AuthorizeOrderQuery;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.OrderList;
import com.zanclick.prepay.web.dto.Orders;
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
@Service("comZanclickQueryAuthOrderList")
public class OrderListServiceImpl extends AbstractCommonService implements ApiRequestResolver {

    @Autowired
    private AuthorizeOrderService authorizeOrderService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("查询成功");
        try {
            String decrypt = verifyCipherJson(appId, cipherJson);
            JSONObject object = JSONObject.parseObject(decrypt);
            AuthorizeOrderQuery query = parser(request, AuthorizeOrderQuery.class);
            query.setMerchantNo(object.getString("merchantNo"));
            query.setStoreNo(object.getString("storeNo"));
            if (DataUtil.isEmpty(query.getStateIn())) {
                query.setStateIn("1,3,4,5");
            }
            List<AuthorizeOrder> authorizeOrderList = authorizeOrderService.queryList(query);
            Orders list = getOrderList(authorizeOrderList);
            param.setData(list);
            return param.toString();
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
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    private Orders getOrderList(List<AuthorizeOrder> orderList) {
        Orders o = new Orders();
        List<OrderList> orders = new ArrayList<>();
        for (AuthorizeOrder order : orderList) {
            OrderList list = new OrderList();
            list.setId(order.getId());
            list.setAmount(order.getMoney());
            list.setMerchantNo(order.getMerchantNo());
            list.setState(order.getState());
            list.setStateDesc(getStateDesc(order.getState()));
            list.setStoreNo(order.getStoreNo());
            list.setPayTime(sdf.format(order.getCreateTime()));
            list.setOrderNo(order.getRequestNo());
            orders.add(list);
        }
        o.setSize(orders.size());
        o.setList(orders);
        return o;
    }
}

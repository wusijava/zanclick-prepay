package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;

/**
 * 预授权商户
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeOrderService extends BaseService<AuthorizeOrder, Long> {


    /**
     * 根据订单号查询订单
     *
     * @param requestNo
     * @return
     */
    AuthorizeOrder queryByRequestNo(String requestNo);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    AuthorizeOrder queryByOrderNo(String orderNo);


    /**
     * 维护订单状态（主要为超时未关闭的）
     *
     * @param order
     * @return 该订单是否超时
     */
    boolean maintainAuthorizeOrder(AuthorizeOrder order);

    /**
     * 根据订单号查询订单
     *
     * @param outTradeNo
     * @return
     */
    AuthorizeOrder queryByOutTradeNo(String outTradeNo);

    /**
     * 根据授权订单号查询
     *
     * @param authNo
     * @return
     */
    AuthorizeOrder queryByAuthNo(String authNo);

    /**
     * 处理订单信息
     *
     * @param order
     */
    void handleAuthorizeOrder(AuthorizeOrder order);
}

package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;

/**
 * @author Administrator
 * @date 2019-12-04 18:07:00
 **/
public interface RedPacketService extends BaseService<RedPacket,Long> {

    /**
     * 根据外部订单查询
     *
     * @param outOrderNo
     * @return
     */
    RedPacket queryByOutOrderNo(String outOrderNo);

    /**
     * 根据订单查询
     *
     * @param outTradeNo
     * @return
     */
    RedPacket queryByOutTradeNo(String outTradeNo);


    /**
     * 创建红包
     * @param order
     * */
    void createRedPacket(PayOrder order);

    /**
     * 回退红包
     * @param order
     * */
    void refundRedPacket(PayOrder order);

    void updateTypeBySellerNo(String sellerNo, Integer type);

}

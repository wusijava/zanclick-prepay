package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;

/**
 * @author admin
 * @date 2019-11-14 12:01:55
 **/
public interface RedPacketService extends BaseService<RedPacket,Long> {

    /**
     * 根据 orderNo查找
     *
     * @param outTradeNo
     * @return
     */
    RedPacket queryByOutTradeNo(String outTradeNo);

    /**
     * 根据 outOrderNo查找
     *
     * @param outOrderNo
     * @return
     */
    RedPacket queryByOutOrderNo(String outOrderNo);


    /**
     * 同步查询一分钟(数据库里的状态与传入的状态一样的时候停止)
     *
     * @param outTradeNo
     * @param state 原来的处理状态
     * @return
     */
    RedPacket syncQueryState(String outTradeNo,Integer state);

}

package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.RedPacket;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Administrator
 * @date 2019-12-04 18:07:00
 **/
@Mapper
public interface RedPacketMapper extends BaseMapper<RedPacket, Long> {

    /**
     * 根据外部订单查询
     *
     * @param outOrderNo
     * @return
     */
    RedPacket selectByOutOrderNo(String outOrderNo);

    /**
     * 根据订单查询
     *
     * @param outTradeNo
     * @return
     */
    RedPacket selectByOutTradeNo(String outTradeNo);

    void updateTypeBySellerNo(Map<String, Object> map);

}

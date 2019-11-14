package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.vo.RedPacketList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder, Long> {

    /**
     * 根据 outTradeNo查找
     *
     * @param outTradeNo
     * @return
     */
    PayOrder selectByOutTradeNo(String outTradeNo);

    /**
     * 根据 outTradeNo查找
     *
     * @param authNo
     * @return
     */
    PayOrder selectByAuthNo(String authNo);

    /**
     * 根据 outOrderNo查找
     * @param outOrderNo
     * @return
     */
    PayOrder selectByOutOrderNo(String outOrderNo);


}

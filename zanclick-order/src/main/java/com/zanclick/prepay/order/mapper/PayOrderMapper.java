package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-09-26 11:36:03
 **/
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder, Long> {

    /**
     * 根据 orderNo查找
     *
     * @param orderNo
     * @return
     */
    PayOrder selectByOrderNo(String orderNo);

    /**
     * 根据 outOrderNo查找
     *
     * @param outOrderNo
     * @return
     */
    PayOrder selectByOutOrderNo(String outOrderNo);
}
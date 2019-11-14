package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-11-14 11:30:42
 **/
@Mapper
public interface PayRefundOrderMapper extends BaseMapper<PayRefundOrder,Long> {


}

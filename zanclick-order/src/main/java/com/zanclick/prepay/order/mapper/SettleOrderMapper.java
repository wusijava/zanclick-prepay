package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.SettleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-10-31 15:02:59
 **/
@Mapper
public interface SettleOrderMapper extends BaseMapper<SettleOrder,Long> {


}

package com.zanclick.prepay.settle.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.settle.entity.SettleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-10-31 15:47:10
 **/
@Mapper
public interface SettleOrderMapper extends BaseMapper<SettleOrder, Long> {

    /**
     * 根据orderNo查询
     *
     * @return
     * @@param orderNo
     */
    SettleOrder selectByOrderNo(String orderNo);


}

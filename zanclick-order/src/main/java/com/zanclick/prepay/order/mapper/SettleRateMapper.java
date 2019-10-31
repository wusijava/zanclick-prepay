package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.SettleRate;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-10-31 14:21:08
 **/
@Mapper
public interface SettleRateMapper extends BaseMapper<SettleRate, Long> {

    /**
     * 根据appId查询
     *
     * @param appId
     * @return
     */
    SettleRate selectByAppId(String appId);
}

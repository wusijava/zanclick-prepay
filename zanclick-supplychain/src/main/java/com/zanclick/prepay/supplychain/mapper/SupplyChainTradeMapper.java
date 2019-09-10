package com.zanclick.prepay.supplychain.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lvlu
 * @date 2019-07-04 17:55:05
 **/
@Mapper
public interface SupplyChainTradeMapper extends BaseMapper<SupplyChainTrade,Long> {

    /**
     * 根据 authNo
     *
     * @param auth_no
     * @return
     */
    SupplyChainTrade selectByAuthNo(String auth_no);
}

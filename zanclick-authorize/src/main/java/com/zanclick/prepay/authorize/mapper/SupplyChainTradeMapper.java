package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-10-29 17:16:41
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


    /**
     * 根据 outRequestNo
     *
     * @param outRequestNo
     * @return
     */
    SupplyChainTrade selectByOutRequestNo(String outRequestNo);

}

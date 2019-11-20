package com.zanclick.prepay.user.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.user.entity.StoreMark;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-11-20 15:52:13
 **/
@Mapper
public interface StoreMarkMapper extends BaseMapper<StoreMark, Long> {


    /**
     * 根据支付宝账号编码
     *
     * @param aliPayLoginNo
     */
    StoreMark selectByAliPayLoginNo(String aliPayLoginNo);


}

package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.RedPackDetail;
import com.zanclick.prepay.order.mapper.RedPackDetailMapper;
import com.zanclick.prepay.order.service.RedPackDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2019-12-13 16:11:48
 **/
@Service
public class RedPackDetailServiceImpl extends BaseMybatisServiceImpl<RedPackDetail,Long> implements RedPackDetailService {

    @Autowired
    private RedPackDetailMapper redPackDetailMapper;


    @Override
    protected BaseMapper<RedPackDetail, Long> getBaseMapper() {
        return redPackDetailMapper;
    }
}

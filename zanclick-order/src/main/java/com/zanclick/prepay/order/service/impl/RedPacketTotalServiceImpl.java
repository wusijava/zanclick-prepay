package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.RedPacketTotal;
import com.zanclick.prepay.order.mapper.RedPacketTotalMapper;
import com.zanclick.prepay.order.service.RedPacketTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2019-12-13 12:51:13
 **/
@Service
public class RedPacketTotalServiceImpl extends BaseMybatisServiceImpl<RedPacketTotal,Long> implements RedPacketTotalService {

    @Autowired
    private RedPacketTotalMapper redPacketTotalMapper;


    @Override
    protected BaseMapper<RedPacketTotal, Long> getBaseMapper() {
        return redPacketTotalMapper;
    }
}

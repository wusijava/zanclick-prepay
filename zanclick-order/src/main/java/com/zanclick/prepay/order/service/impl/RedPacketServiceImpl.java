package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.mapper.RedPacketMapper;
import com.zanclick.prepay.order.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2019-11-13 19:59:21
 **/
@Service
public class RedPacketServiceImpl extends BaseMybatisServiceImpl<RedPacket,Long> implements RedPacketService {

    @Autowired
    private RedPacketMapper redPacketMapper;


    @Override
    protected BaseMapper<RedPacket, Long> getBaseMapper() {
        return redPacketMapper;
    }
}

package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.mapper.RedPacketMapper;
import com.zanclick.prepay.order.query.RedPacketQuery;
import com.zanclick.prepay.order.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 * @date 2019-11-14 12:01:55
 **/
@Service
public class RedPacketServiceImpl extends BaseMybatisServiceImpl<RedPacket,Long> implements RedPacketService {

    @Autowired
    private RedPacketMapper redPacketMapper;


    @Override
    protected BaseMapper<RedPacket, Long> getBaseMapper() {
        return redPacketMapper;
    }

    @Override
    public RedPacket queryByOutTradeNo(String outTradeNo) {
        RedPacketQuery query = new RedPacketQuery();
        query.setOutTradeNo(outTradeNo);
        List<RedPacket> packetList = this.queryList(query);
        return packetList == null ? null : packetList.get(0);
    }

    @Override
    public RedPacket queryByOutOrderNo(String outOrderNo) {
        RedPacketQuery query = new RedPacketQuery();
        query.setOutOrderNo(outOrderNo);
        List<RedPacket> packetList = this.queryList(query);
        return packetList == null ? null : packetList.get(0);
    }
}

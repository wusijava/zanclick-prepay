package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.mapper.RedPacketRecordMapper;
import com.zanclick.prepay.order.query.RedPacketRecordQuery;
import com.zanclick.prepay.order.service.RedPacketRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 * @date 2019-11-14 12:01:55
 **/
@Service
public class RedPacketRecordServiceImpl extends BaseMybatisServiceImpl<RedPacketRecord,Long> implements RedPacketRecordService {

    @Autowired
    private RedPacketRecordMapper redPacketRecordMapper;


    @Override
    protected BaseMapper<RedPacketRecord, Long> getBaseMapper() {
        return redPacketRecordMapper;
    }

    @Override
    public RedPacketRecord queryByOutTradeNo(String outTradeNo) {
        RedPacketRecordQuery query = new RedPacketRecordQuery();
        query.setOutTradeNo(outTradeNo);
        List<RedPacketRecord> packetList = this.queryList(query);
        return packetList == null || packetList.size() == 0 ? null : packetList.get(0);
    }

    @Override
    public RedPacketRecord queryByOutOrderNo(String outOrderNo) {
        RedPacketRecordQuery query = new RedPacketRecordQuery();
        query.setOutOrderNo(outOrderNo);
        List<RedPacketRecord> packetList = this.queryList(query);
        return packetList == null || packetList.size() == 0 ? null : packetList.get(0);
    }

    @Override
    public RedPacketRecord syncQueryState(String outTradeNo, Integer state) {
        int times = 30;
        for (int i = 0; i <= times; i++) {
            RedPacketRecord packet = this.queryByOutTradeNo(outTradeNo);
            if (packet != null && !state.equals(packet.getState())) {
                return packet;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

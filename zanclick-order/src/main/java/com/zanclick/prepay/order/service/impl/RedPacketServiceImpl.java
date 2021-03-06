package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.mapper.RedPacketMapper;
import com.zanclick.prepay.order.service.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2019-12-04 18:07:00
 **/
@Slf4j
@Service
public class RedPacketServiceImpl extends BaseMybatisServiceImpl<RedPacket,Long> implements RedPacketService {

    @Autowired
    private RedPacketMapper redPacketMapper;


    @Override
    protected BaseMapper<RedPacket, Long> getBaseMapper() {
        return redPacketMapper;
    }

    @Override
    public RedPacket queryByOutOrderNo(String outOrderNo) {
        return redPacketMapper.selectByOutOrderNo(outOrderNo);
    }

    @Override
    public RedPacket queryByOutTradeNo(String outTradeNo) {
        return redPacketMapper.selectByOutTradeNo(outTradeNo);
    }

    @Override
    public void createRedPacket(PayOrder order) {
        RedPacket packet = this.queryByOutTradeNo(order.getOutTradeNo());
        if (packet != null){
            log.error("红包,重复发放:{}",order.getOutTradeNo());
            return;
        }
        if (!MoneyUtil.largeMoney(order.getRedPackAmount(),"0.00")){
            log.error("红包,金额为0.00:{}",order.getOutTradeNo());
            return;
        }
        packet = new RedPacket();
        packet.setTitle(order.getTitle());
        packet.setOutTradeNo(order.getOutTradeNo());
        packet.setOutOrderNo(order.getOutOrderNo());
        packet.setWayId(order.getWayId());
        packet.setState(RedPacket.State.waiting.getCode());
        packet.setMerchantNo(order.getMerchantNo());
        packet.setType(order.getRedPackType());
        packet.setCreateTime(new Date());
        packet.setAmount(order.getRedPackAmount());
        packet.setAppId(order.getAppId());
        packet.setSellerNo(order.getSellerNo());
        packet.setName(order.getName());
        getBaseMapper().insert(packet);
    }

    @Override
    public void refundRedPacket(PayOrder order) {
        RedPacket packet = this.queryByOutTradeNo(order.getOutTradeNo());
        if (packet != null && packet.getState().equals(RedPacket.State.waiting.getCode())){
           packet.setState(PayOrder.State.refund.getCode());
           packet.setFinishTime(new Date());
           this.updateById(packet);
        }

    }

    @Override
    public void updateTypeBySellerNo(String sellerNo, Integer type) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("sellerNo", sellerNo);
        redPacketMapper.updateTypeBySellerNo(map);
    }

    @Override
    public List<Map<String, Object>> getSettleList(RedPacket RedPacket) {
        return redPacketMapper.getSettleList(RedPacket);
    }

    @Override
    public List<Map<String, Object>> getSettleDetail(RedPacket redPacket) {
        return redPacketMapper.getSettleDetail(redPacket);
    }
}

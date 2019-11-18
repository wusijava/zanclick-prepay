package com.zanclick.prepay.settle.listener;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.util.AuthorizePayUtil;
import com.zanclick.prepay.authorize.vo.Transfer;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.query.RedPacketQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 商户进阶通过，
 *
 * @author duchong
 * @date 2019-6-26 10:17:26
 */
@Slf4j
@Component
public class PayOrderRedPacketListener {

    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @JmsListener(destination = JmsMessaging.ORDER_RED_PACKET_MESSAGE)
    public void getMessage(String message) {
        PayOrder order = payOrderService.queryByOutTradeNo(message);
        if (order == null){
            log.error("订单编号异常:{}", message);
            return ;
        }
        if (order.getRedPackState().equals(PayOrder.RedPackState.receive.getCode())){
            log.error("红包已领取:{}", message);
            return ;
        }
        RedPacketQuery query = new RedPacketQuery();
        query.setOutTradeNo(message);
        List<RedPacket> packetList = redPacketService.queryList(query);
        if (DataUtil.isEmpty(packetList)) {
            log.error("红包数据异常:{}", message);
            return ;
        }
        List<RedPacket> successList = new ArrayList<>();
        List<RedPacket> waitList = new ArrayList<>();
        for (RedPacket packet : packetList) {
            if (packet.getState().equals(RedPacket.State.success.getCode())) {
                successList.add(packet);
            } else if (packet.getState().equals(RedPacket.State.waiting.getCode())) {
                waitList.add(packet);
            }
        }
        if (DataUtil.isNotEmpty(successList) && DataUtil.isNotEmpty(waitList)) {
            for (RedPacket packet:waitList){
                packet.setState(RedPacket.State.failed.getCode());
                packet.setReason("重复领取");
                redPacketService.updateById(packet);
            }
        }
        if (DataUtil.isEmpty(successList) && DataUtil.isNotEmpty(waitList)) {
            Boolean flag = true;
            for (RedPacket packet:waitList){
                if (flag){
                    transfer(packet,order);
                    flag = false;
                }else {
                    packet.setState(RedPacket.State.failed.getCode());
                    packet.setReason("重复领取");
                    redPacketService.updateById(packet);
                }
            }
        }
    }

    private void transfer(RedPacket packet,PayOrder order){
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(30L);
        packet.setBizNo(StringUtils.getTradeNo());
        Transfer transfer = new Transfer();
        transfer.setAmount(packet.getAmount());
        transfer.setOut_biz_no(packet.getBizNo());
        transfer.setPayee_account(packet.getReceiveNo());
        transfer.setPayee_type("ALIPAY_LOGONID");
        transfer.setPayer_show_name("点赞科技有限公司");
        transfer.setRemark(packet.getOutOrderNo()+"_红包");
        AlipayFundTransToaccountTransferResponse response = AuthorizePayUtil.transfer(client,transfer);
        if (response.isSuccess()){
            packet.setState(RedPacket.State.success.getCode());
            packet.setPayNo(response.getOrderId());
            redPacketService.updateById(packet);
            order.setRedPackState(PayOrder.RedPackState.receive.getCode());
            order.setRedPackSellerNo(packet.getReceiveNo());
            payOrderService.updateById(order);
        }else {
            packet.setState(RedPacket.State.failed.getCode());
            packet.setReason(response.getSubMsg());
            redPacketService.updateById(packet);
        }
    }

}


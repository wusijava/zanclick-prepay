package com.zanclick.prepay.order.listener;

import com.alibaba.fastjson.JSONObject;
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
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.query.RedPacketRecordQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.RedPacketRecordService;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.order.vo.ReceiveRedPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    private RedPacketRecordService redPacketRecordService;
    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @JmsListener(destination = JmsMessaging.ORDER_RED_PACKET_MESSAGE)
    public void getMessage(String message) {
        ReceiveRedPacket receive = JSONObject.parseObject(message, ReceiveRedPacket.class);
        RedPacket packet = redPacketService.queryByOutOrderNo(receive.getOutOrderNo());
        if (packet != null && !packet.getState().equals(RedPacket.State.waiting.getCode())) {
            log.error("红包状态异常:{}", message);
            return;
        }
        RedPacketRecordQuery query = new RedPacketRecordQuery();
        query.setOutOrderNo(receive.getOutOrderNo());
        query.setState(RedPacketRecord.State.success.getCode());
        Long packetList = redPacketRecordService.queryCount(query);
        if (packetList != null && packetList > 0) {
            log.error("红包已领取:{}", message);
            return;
        }
        PayOrder order = payOrderService.queryByOutOrderNo(receive.getOutOrderNo());
        RedPacketRecord record = createRedPacket(receive, packet);
        transfer(record, packet, order);
    }


    /**
     * 创建领取记录
     *
     * @param packet
     * @param packet
     * @return
     */
    private RedPacketRecord createRedPacket(ReceiveRedPacket receive, RedPacket packet) {
        RedPacketRecord record = new RedPacketRecord();
        record.setAmount(packet.getAmount());
        record.setAppId(packet.getAppId());
        record.setCreateTime(new Date());
        record.setMerchantNo(packet.getMerchantNo());
        record.setOutOrderNo(packet.getOutOrderNo());
        record.setOutTradeNo(packet.getOutTradeNo());
        record.setWayId(packet.getWayId());
        record.setState(0);
        record.setTitle(packet.getTitle());
        record.setReceiveNo(receive.getReceiveNo());
        record.setName(receive.getName());
        redPacketRecordService.insert(record);
        return record;
    }


    /**
     * 转账接口
     *
     * @param record
     * @param packet
     * @param order
     * @return
     */
    private void transfer(RedPacketRecord record, RedPacket packet, PayOrder order) {
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(30L);
        record.setBizNo(StringUtils.getTradeNo());
        Transfer transfer = new Transfer();
        transfer.setAmount(record.getAmount());
        transfer.setOut_biz_no(record.getBizNo());
        transfer.setPayee_account(record.getReceiveNo());
        transfer.setPayee_type("ALIPAY_LOGONID");
        transfer.setPayer_show_name("点赞科技有限公司");
        transfer.setRemark(record.getOutOrderNo() + "_红包");
        if (DataUtil.isNotEmpty(record.getName())) {
            transfer.setPayee_real_name(record.getName());
        }
        AlipayFundTransToaccountTransferResponse response = AuthorizePayUtil.transfer(client, transfer);
        if (response.isSuccess()) {
            record.setState(RedPacketRecord.State.success.getCode());
            record.setPayNo(response.getOrderId());
            redPacketRecordService.updateById(record);
            order.setRedPackState(PayOrder.RedPackState.receive.getCode());
            order.setRedPackSellerNo(record.getReceiveNo());
            payOrderService.updateById(order);
            packet.setState(RedPacket.State.success.getCode());
            packet.setFinishTime(new Date());
            redPacketService.updateById(packet);
        } else {
            record.setState(RedPacketRecord.State.failed.getCode());
            record.setReason(response.getSubMsg());
            redPacketRecordService.updateById(record);
        }
    }

}


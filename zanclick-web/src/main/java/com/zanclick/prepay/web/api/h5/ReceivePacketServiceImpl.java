package com.zanclick.prepay.web.api.h5;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.QueryRedPacket;
import com.zanclick.prepay.web.dto.ReceiveRedPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 领取红包接口
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service("comZanclickReceivePacket")
public class ReceivePacketServiceImpl extends AbstractCommonService implements ApiRequestResolver {
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private RedPacketService redPacketService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("领取成功");
        try {
            ReceiveRedPacket query = parser(request, ReceiveRedPacket.class);
            PayOrder order = queryOrder(query);
            createRedPacket(order,query);
            return param.toString();
        } catch (BizException be) {
            param.setMessage(be.getMessage());
            log.error("查询异常:{}", be);
        } catch (Exception e) {
            param.setMessage("系统异常，请稍后再试");
            log.error("系统异常:{}", e);
        }
        param.setFail();
        return param.toString();
    }


    /**
     * 根据订单号查询
     *
     * @param query
     * @return
     */
    private PayOrder queryOrder(ReceiveRedPacket query) {
        PayOrder order = payOrderService.queryRedPacketOrder(query.getOutOrderNo());
        if (!order.getWayId().equals(query.getWayId())) {
            log.error("无法领取其他门店的红包:{}", query.getWayId());
            throw new BizException("渠道编码不正确");
        }
        if (DataUtil.isEmpty(query.getReceiveNo())) {
            log.error("请填写领取账号:{}", query.getReceiveNo());
            throw new BizException("请填写领取支付宝账号");
        }
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(order.getMerchantNo());
        if (!merchant.isSuccess()) {
            log.error("商户注册状态异常:{}", merchant.getWayId());
            throw new BizException("商户注册状态异常");
        }
        if (AuthorizeMerchant.RedPackState.closed.getCode().equals(merchant.getRedPackState())) {
            log.error("本商户已关闭领取红包权限:{}", merchant.getWayId());
            throw new BizException("当前商户暂无法领取红包");
        }
        if (DataUtil.isNotEmpty(merchant.getRedPackSellerNo()) && !query.getReceiveNo().trim().equals(merchant.getRedPackSellerNo())) {
            log.error("本商户已指定红包领取账号，请使用指定账号领取:{}", merchant.getWayId());
            throw new BizException("本商户已指定红包领取账号，请使用指定账号领取");
        }
        return order;
    }


    /**
     * 红包领取
     *
     * @param order
     * @param receive
     */
    private void createRedPacket(PayOrder order, ReceiveRedPacket receive) {
        RedPacket packet = redPacketService.queryByOutOrderNo(receive.getOutOrderNo());
        if (packet == null || packet.getState().equals(RedPacket.State.failed.getCode())){
            packet = new RedPacket();
            packet.setAmount(order.getRedPackAmount());
            packet.setAppId(order.getAppId());
            packet.setCreateTime(new Date());
            packet.setMerchantNo(order.getMerchantNo());
            packet.setOutOrderNo(order.getOutOrderNo());
            packet.setOutTradeNo(order.getOutTradeNo());
            packet.setWayId(order.getWayId());
            packet.setState(0);
            packet.setTitle(order.getTitle());
            packet.setReceiveNo(receive.getReceiveNo());
            redPacketService.insert(packet);
            SendMessage.sendMessage(JmsMessaging.ORDER_RED_PACKET_MESSAGE,order.getOutTradeNo());
            RedPacket redPacket = redPacketService.syncQueryState(order.getOutTradeNo(),packet.getState());
            if (redPacket == null){
                log.error("未知错误，:{}", receive.getOutOrderNo());
                throw new BizException("未知错误");
            }
            if (redPacket.getState().equals(RedPacket.State.failed.getCode())){
                log.error("红包发放错误，:{}", redPacket.getReason());
                throw new BizException("红包发放出错，"+redPacket.getReason());
            }
        }else if (RedPacket.State.success.getCode().equals(packet.getState())){
            log.error("单笔订单红包只可以领取一次:{}", receive.getOutOrderNo());
            throw new BizException("单笔订单红包只可以领取一次");
        }else if (RedPacket.State.waiting.getCode().equals(packet.getState())){
            log.error("红包放款中,请稍后:{}", receive.getOutOrderNo());
            throw new BizException("红包放款中,请稍后");
        }
    }

}

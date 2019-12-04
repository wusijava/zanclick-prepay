package com.zanclick.prepay.web.api.h5;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.service.RedPacketRecordService;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.web.api.AbstractCommonService;
import com.zanclick.prepay.web.dto.ReceiveRedPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private RedPacketService redPacketService;
    @Autowired
    private RedPacketRecordService redPacketRecordService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Override
    public String resolve(String appId, String cipherJson, String request) {
        ResponseParam param = new ResponseParam();
        param.setSuccess();
        param.setMessage("领取成功");
        try {
            ReceiveRedPacket query = parser(request, ReceiveRedPacket.class);
            String check = query.check();
            if (check != null){
                param.setMessage(check);
                param.setFail();
                return param.toString();
            }
            queryRedPacket(query);
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
     * 红包领取
     *
     * @param receive
     */
    private void queryRedPacket(ReceiveRedPacket receive) {
        RedPacket packet = redPacketService.queryByOutOrderNo(receive.getOutOrderNo());
        if (packet == null){
            log.error("未找到该红包,无法领取:{}", receive.getOutOrderNo());
            throw new BizException("红包发放中，请稍后");
        }
        if (!packet.getType().equals(RedPacket.Type.personal.getCode())){
            log.error("红包类型不正确:{}", receive.getOutOrderNo());
            throw new BizException("该红包为"+packet.getTypeDesc()+"红包");
        }
        if (packet.getState().equals(RedPacket.State.success.getCode())){
            log.error("红包已领取:{}", receive.getOutOrderNo());
            throw new BizException("红包已领取");
        }
        if (packet.getState().equals(RedPacket.State.refund.getCode())){
            log.error("红包已退还:{}", receive.getOutOrderNo());
            throw new BizException("红包已退还");
        }
        if (!receive.getWayId().trim().equals(packet.getWayId().trim())){
            log.error("渠道编号不正确:{},{}", receive.getWayId(),packet.getWayId());
            throw new BizException("渠道编号不正确");
        }
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(packet.getMerchantNo());
        if (!merchant.isSuccess()) {
            log.error("商户注册状态异常:{}", merchant.getWayId());
            throw new BizException("商户注册状态异常");
        }
        if (AuthorizeMerchant.RedPackState.closed.getCode().equals(merchant.getRedPackState())) {
            log.error("本商户已关闭领取红包权限:{}", merchant.getWayId());
            throw new BizException("当前商户暂无法领取红包");
        }
        if (DataUtil.isNotEmpty(merchant.getRedPackSellerNo()) && !receive.getReceiveNo().trim().equals(merchant.getRedPackSellerNo().trim())) {
            log.error("本商户已指定红包领取账号，请使用指定账号领取:{}", merchant.getWayId());
            throw new BizException("本商户已指定红包领取账号，请使用指定账号领取");
        }
        if (packet != null && packet.getState().equals(RedPacketRecord.State.waiting.getCode())){
            SendMessage.sendMessage(JmsMessaging.ORDER_RED_PACKET_MESSAGE,JSONObject.toJSONString(receive));
            RedPacketRecord record = redPacketRecordService.syncQueryState(packet.getOutTradeNo(),RedPacketRecord.State.waiting.getCode());
            if (record != null && record.getState().equals(RedPacketRecord.State.failed.getCode())){
                log.error("红包发放失败:{},{}", receive.getOutOrderNo(),record.getReason());
                throw new BizException(record.getReason());
            }
            if (record == null){
                log.error("红包发放失败:{}", receive.getOutOrderNo());
                throw new BizException("红包发放失败");
            }
        }
    }

}

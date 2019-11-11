package com.zanclick.prepay.authorize.event;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.utils.DataUtil;

/**
 * @author lvlu
 * @date 2019-05-10 11:57
 **/
public abstract class AbstractBaseEventResolver {

    /**
     * 推送结算成功/失败的消息
     *
     * @param state  与payorder里的 dealstate相同
     * @param reason
     * @param authNo
     */
    protected void sendMessage(Integer state, String reason, String authNo) {
        JSONObject object = new JSONObject();
        object.put("state",state);
        object.put("authNo",authNo);
        if (DataUtil.isNotEmpty(reason)){
            object.put("reason",reason);
        }
        SendMessage.sendMessage(JmsMessaging.ORDER_SETTLE_MESSAGE,object.toJSONString());
    }

}

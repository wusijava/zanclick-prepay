package com.zanclick.prepay.common.config;

/**
 * @desciption 消息中间件配置信息初始化
 * @author duchong
 * @date 2018-11-7 10:59:09
 * @version v1.0.0
 **/
public class JmsMessaging {
    /**
     * 推送，结算
     * */
    public final static String ORDER_NOTIFY_MESSAGE = "order.notify.message";

    /**
     * 结算完成
     * */
    public final static String ORDER_SETTLE_MESSAGE = "order.settle.message";
}
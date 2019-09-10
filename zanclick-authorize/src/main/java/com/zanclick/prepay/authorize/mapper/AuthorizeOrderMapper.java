package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;

/**
 * 直付通预授权订单
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeOrderMapper extends BaseMapper<AuthorizeOrder, Long> {

    /**
     * 根据id更新二维码和请求内容
     *
     * @param order
     * @retuan
     */
    void updateQrCodeAndContent(AuthorizeOrder order);

    /**
     * 根据订单号查询订单
     *
     * @param requestNo
     * @return
     */
    AuthorizeOrder selectByRequestNo(String requestNo);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    AuthorizeOrder selectByOrderNo(String orderNo);

    /**
     * 根据订单号查询订单
     *
     * @param outTradeNo
     * @return
     */
    AuthorizeOrder selectByOutTradeNo(String outTradeNo);

    /**
     * 根据授权订单号
     *
     * @param authNo
     * @return
     */
    AuthorizeOrder selectByAuthNo(String authNo);

}

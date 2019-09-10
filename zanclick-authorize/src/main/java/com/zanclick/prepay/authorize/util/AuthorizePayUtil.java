package com.zanclick.prepay.authorize.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.*;
import com.zanclick.prepay.authorize.pay.dto.AuthorizeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预授权支付工具类
 *
 * @author duchong
 * @date 2019-7-9 10:44:42
 */
public class AuthorizePayUtil {

    private static Logger _log = LoggerFactory.getLogger(AuthorizePayUtil.class);

    /**
     * 二维码资金授权冻结
     *
     * @param client
     * @param dto
     * @return
     */
    public static AlipayFundAuthOrderVoucherCreateResponse qrFreeze(AlipayClient client, String notifyUrl, AuthorizeDTO dto){
        return AuthorizeClientUtil.qrFreeze(client,null,notifyUrl,dto.toString());
    }

    /**
     * 资金授权解冻
     *
     * @param client
     * @param dto
     * @return
     */
    public static AlipayFundAuthOrderUnfreezeResponse unFreeze(AlipayClient client, AuthorizeDTO dto){
        return AuthorizeClientUtil.unFreeze(client,null,dto.toString());
    }

    /**
     * 资金转支付
     *
     * @param client
     * @param dto
     * @return
     */
    public static AlipayTradePayResponse pay(AlipayClient client,String notifyUrl ,AuthorizeDTO dto){
        return AuthorizeClientUtil.pay(client,null,notifyUrl,dto.toString());
    }


    /**
     * 资金授权冻结撤销
     *
     * @param client
     * @param dto
     * @return
     */
    public static boolean cancel(AlipayClient client, AuthorizeDTO dto){
        AlipayFundAuthOperationCancelResponse cancelResponse = AuthorizeClientUtil.cancel(client,null,dto.toString());
        return cancelResponse.isSuccess();
    }

    /**
     * 资金授权冻结查询
     *
     * @param client
     * @param dto
     * @return
     */
    public static AlipayFundAuthOperationDetailQueryResponse query(AlipayClient client, AuthorizeDTO dto){
        AlipayFundAuthOperationDetailQueryResponse queryResponse = AuthorizeClientUtil.query(client,null,dto.toString());
        return queryResponse;
    }


    /**
     * 转支付退款
     *
     * @param client
     * @param dto
     * @return
     */
    public static AlipayTradeRefundResponse payRefund(AlipayClient client, AuthorizeDTO dto){
        return AuthorizeClientUtil.refund(client,null,dto.toString());
    }

}

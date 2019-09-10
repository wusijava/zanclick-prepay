package com.zanclick.prepay.authorize.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预授权支付工具类
 *
 * @author duchong
 * @date 2019-7-9 10:44:42
 */
public class AuthorizeClientUtil {

    private static Logger _log = LoggerFactory.getLogger(AuthorizeClientUtil.class);

    /**
     * 扫码资金授权冻结
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayFundAuthOrderFreezeResponse scanFreeze(AlipayClient client, String appAuthToken, String biz_content) {
        AlipayFundAuthOrderFreezeRequest request = new AlipayFundAuthOrderFreezeRequest();
        request.setBizContent(biz_content);
        AlipayFundAuthOrderFreezeResponse response = null;
        try {
            _log.error("扫码资金冻结:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("扫码资金冻结结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("扫码资金冻结异常：{}", e);
        }
        if (response == null) {
            response = new AlipayFundAuthOrderFreezeResponse();
//            response.setCode(AliConstants.ERROR);
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
        }
        return response;
    }

    /**
     * 二维码资金授权冻结
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayFundAuthOrderVoucherCreateResponse qrFreeze(AlipayClient client, String appAuthToken, String notifyUrl, String biz_content){
        AlipayFundAuthOrderVoucherCreateRequest request = new AlipayFundAuthOrderVoucherCreateRequest();
        request.setBizContent(biz_content);
        request.setNotifyUrl(notifyUrl);
        AlipayFundAuthOrderVoucherCreateResponse response = null;
        try {
            _log.error("二维码资金冻结:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("二维码资金冻结结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("二维码资金冻结异常：{}", e);
        }
        if (response == null) {
            response = new AlipayFundAuthOrderVoucherCreateResponse();
//            response.setCode(AliConstants.ERROR);
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
        }
        return response;
    }

    /**
     * 资金冻结查询
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayFundAuthOperationDetailQueryResponse query(AlipayClient client, String appAuthToken, String biz_content){
        AlipayFundAuthOperationDetailQueryRequest request = new AlipayFundAuthOperationDetailQueryRequest();
        request.setBizContent(biz_content);
        AlipayFundAuthOperationDetailQueryResponse response = null;
        try {
            _log.error("资金冻结查询:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("资金冻结查询结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("资金冻结查询异常：{}", e);
        }
        if (response == null) {
            response = new AlipayFundAuthOperationDetailQueryResponse();
//            response.setCode(AliConstants.ERROR);
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
        }
        return response;
    }

    /**
     * 资金冻结撤销
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayFundAuthOperationCancelResponse cancel(AlipayClient client, String appAuthToken, String biz_content){
        AlipayFundAuthOperationCancelRequest request = new AlipayFundAuthOperationCancelRequest();
        request.setBizContent(biz_content);
        AlipayFundAuthOperationCancelResponse response = null;
        try {
            _log.error("资金冻结撤销:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("资金冻结撤销结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("资金冻结撤销异常：{}", e);
        }
        if (response == null) {
            response = new AlipayFundAuthOperationCancelResponse();
//            response.setCode(AliConstants.ERROR);
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
        }
        return response;
    }

    /**
     * 资金解冻
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayFundAuthOrderUnfreezeResponse unFreeze(AlipayClient client, String appAuthToken, String biz_content) {
        AlipayFundAuthOrderUnfreezeRequest request = new AlipayFundAuthOrderUnfreezeRequest();
        request.setBizContent(biz_content);
        AlipayFundAuthOrderUnfreezeResponse response = null;
        try {
            _log.error("资金解冻:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("资金解冻结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("资金解冻异常：{}", e);
        }
        if (response == null) {
            response = new AlipayFundAuthOrderUnfreezeResponse();
//            response.setCode(AliConstants.ERROR);
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
        }
        return response;
    }


    /**
     * 冻结资金转支付
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayTradePayResponse pay(AlipayClient client, String appAuthToken, String notifyUrl, String biz_content){
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setBizContent(biz_content);
        request.setNotifyUrl(notifyUrl);
        AlipayTradePayResponse response = null;
        try {
            _log.error("冻结资金转支付:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("冻结资金转支付结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("冻结资金转支付异常：{}", e);
        }
        if (response == null) {
            response = new AlipayTradePayResponse();
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
//            response.setCode(AliConstants.ERROR);
        }
        return response;
    }

    /**
     * 转支付资金退款
     *
     * @param client
     * @param biz_content
     * @param appAuthToken
     * @return
     */
    public static AlipayTradeRefundResponse refund(AlipayClient client, String appAuthToken, String biz_content){
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(biz_content);
        AlipayTradeRefundResponse response = null;
        try {
            _log.error("转支付资金退款:{}", JSONObject.toJSONString(request));
            response = client.execute(request, null, appAuthToken);
            _log.error("转支付资金退款结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("转支付资金退款异常：{}", e);
        }
        if (response == null) {
            response = new AlipayTradeRefundResponse();
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
//            response.setCode(AliConstants.ERROR);
        }
        return response;
    }

    /**
     * 获取授权码
     *
     * @param client
     * @param biz_content
     * @return
     */
    public static AlipayOpenAuthTokenAppResponse appAuthToken(AlipayClient client, String biz_content){
        AlipayOpenAuthTokenAppRequest request = new AlipayOpenAuthTokenAppRequest();
        request.setBizContent(biz_content);
        AlipayOpenAuthTokenAppResponse response = null;
        try {
            _log.error("获取授权码:{}", JSONObject.toJSONString(request));
            response = client.execute(request);
            _log.error("获取授权码结果:{}", JSONObject.toJSONString(request));
        }catch (Exception e){
            _log.error("获取授权码异常：{}", e);
        }
        if (response == null) {
            response = new AlipayOpenAuthTokenAppResponse();
            response.setMsg("请求失败");
            response.setSubCode("SYSTEM_ERROR");
            response.setSubMsg("系统繁忙，稍后再试");
//            response.setCode(AliConstants.ERROR);
        }
        return response;
    }

//    public static void main(String[] args) {
//        /**转支付*/
//        Pay pay = new Pay();
//        pay.setAuth_confirm_mode("COMPLETE");
//        pay.setAuth_no("2018110510002001720276690628");
//        pay.setBody("资金授权转支付100元");
//        pay.setSubject("资金授权转支付100元");
//        pay.setBuyer_id("2088702474876722");
//        pay.setOut_request_no(StringUtils.createTradeNo());
//        pay.setOut_trade_no(StringUtils.createTradeNo());
//        pay.setProduct_code("PRE_AUTH");
//        pay.setTotal_amount("100.00");
//        pay.setSeller_id("2088111514025756");
//        pay.setStore_id("ZB20181105143310");
//        pay.setTerminal_id("2048331");
//        pay.setTimeout_express("10m");
//        pay.setScene("bar_code");
//        ExtendParam param = new ExtendParam();
//        param.setSys_service_provider_id("2088721239794361");
//        pay.setExtra_param(param);
//        System.err.println("---------------转支付--------------");
//        System.err.println(pay.toString());
//        /**转支付退款*/
//        pay = new Pay();
//        pay.setOut_request_no(StringUtils.createTradeNo());
//        pay.setOut_trade_no(StringUtils.createTradeNo());
//        pay.setRefund_amount("100.00");
//        pay.setRefund_reason("资金授权转支付退款100元");
//        System.err.println("---------------转支付退款--------------");
//        System.err.println(pay.toString());
//        /**资金冻结*/
//        Freeze freeze = new Freeze();
//        freeze.setAuth_code("28763443825664394");
//        freeze.setAuth_code_type("bar_code");
//        freeze.setOut_request_no(StringUtils.createTradeNo());
//        freeze.setOut_order_no(StringUtils.createTradeNo());
//        freeze.setOrder_title("预授权冻结测试");
//        freeze.setAmount("200.00");
//        freeze.setPayee_user_id("2088111514025756");
//        freeze.setPay_timeout("10m");
//        freeze.setProduct_code("PRE_AUTH");
//        System.err.println("---------------资金冻结--------------");
//        System.err.println(freeze.toString());
//        /**资金授权发码*/
//        freeze = new Freeze();
//        freeze.setOut_request_no(StringUtils.createTradeNo());
//        freeze.setOut_order_no(StringUtils.createTradeNo());
//        freeze.setOrder_title("预授权冻结测试");
//        freeze.setAmount("200.00");
//        freeze.setPayee_user_id("2088111514025756");
//        freeze.setPay_timeout("10m");
//        freeze.setProduct_code("PRE_AUTH");
//        freeze.setSettle_currency("USD");
//        freeze.setTrans_currency("USD");
//        param = new ExtendParam();
//        param.setCategory("HOTEL");
//        param.setOutStoreCode("ZB20181105143310");
//        param.setOutStoreAlias("alis");
//        freeze.setExtra_param(param);
//        PayChannelType type1 = new PayChannelType();
//        type1.setPayChannelType("PCREDIT_PAY");
//        PayChannelType type2 = new PayChannelType();
//        type2.setPayChannelType("MONEY_FUND");
//        PayChannelType[] types = {type1,type2};
//        freeze.setEnable_pay_channels(types);
//        System.err.println("---------------资金授权发码--------------");
//        System.err.println(freeze.toString());
//        /**资金授权查询*/
//        freeze = new Freeze();
//        freeze.setOut_request_no(StringUtils.createTradeNo());
//        freeze.setOut_order_no(StringUtils.createTradeNo());
//        System.err.println("---------------资金授权查询--------------");
//        System.err.println(freeze.toString());
//        /**资金授权解冻*/
//        freeze = new Freeze();
//        freeze.setAuth_no("2018110510002001720283364266");
//        freeze.setOut_request_no(StringUtils.createTradeNo());
//        freeze.setRemark("预授权最后解冻100元");
//        freeze.setAmount("100.00");
//        param = new ExtendParam();
//        UnfreezeBizInfo unfreezeBizInfo = new UnfreezeBizInfo();
//        unfreezeBizInfo.setBizComplete("true");
//        param.setUnfreezeBizInfo(unfreezeBizInfo);
//        freeze.setExtra_param(param);
//        System.err.println("---------------资金授权解冻--------------");
//        System.err.println(freeze.toString());
//    }

}

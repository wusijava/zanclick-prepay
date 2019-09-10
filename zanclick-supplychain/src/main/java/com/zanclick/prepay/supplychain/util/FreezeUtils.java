package com.zanclick.prepay.supplychain.util;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.supplychain.config.SupplyChainConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlu
 * @date 2018-11-05 11:57
 **/
public class FreezeUtils {

    private static final String APPAUTHTOKEN = null;

    private static final String NOTIFY_URL = "http:///zanbei.ngrok.etyanzhi.com/notify";

    public static void main(String[] args) throws AlipayApiException {
        String auth_no = "2019051810002001250201616409";
        String unfreezeAmount = "0.01";
        String remark = "解冻转支付0.01元后全部解冻";
        String out_order_no = StringUtils.getTradeNo();
        String out_request_no = StringUtils.getTradeNo();
        tradeRefund("测试退款","0.01","155842386933376735333","155842386933703287716");

//        tradePay(auth_no,unfreezeAmount,remark,out_order_no,out_request_no,"2088231616604616","2088402847876251","zb001","zb001",true);
//        qrcodeFreeze();
    }


    public static void testQuery(){
        String out_order_no = "155721800293620894612";
        String out_request_no = "155721800293900073040";
        System.out.println(out_order_no);
        System.out.println(out_request_no);
        try {
            AlipayResponse response = queryFreeze(out_order_no,out_request_no);
            System.out.println(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    public static void qrcodeFreeze(){
        String auth_code = "280279571270923568";
        String amount = "10";
        String out_order_no = StringUtils.getTradeNo();
        String out_request_no = StringUtils.getTradeNo();
        System.out.println(out_order_no);
        System.out.println(out_request_no);
//        String payee_user_id = SupplyChainConfig.USER_ID;
//        String payee_user_id = "2088201953997687";
        String payee_user_id = "2088231616604616";
        String title = "预授权资金冻结测试100元";
        try {
            AlipayResponse response = barcodeFreeze(auth_code,amount,out_order_no,out_request_no,payee_user_id,title);
            System.out.println(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫码资金授权冻结,暂时只提供余额宝
     */
    public static AlipayFundAuthOrderFreezeResponse barcodeFreeze(String auth_code, String amount, String out_order_no, String out_request_no,
                                                                  String payee_user_id, String title) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayFundAuthOrderFreezeRequest request = new AlipayFundAuthOrderFreezeRequest();
        String bizContent = "{" +
                "\"auth_code\":\"" + auth_code + "\"," +
                "\"auth_code_type\":\"bar_code\"," +
                "\"out_order_no\":\"" + out_order_no + "\"," +
                "\"out_request_no\":\"" + out_request_no + "\"," +
                "\"order_title\":\"" + title + "\"," +
                "\"amount\":" + amount + "," +
                "\"payee_user_id\":\"" + payee_user_id + "\"," +
                "\"pay_timeout\":\"1m\"," +
                "\"product_code\":\"PRE_AUTH\"," +
                "\"extend_params\":\"{\\\"sys_service_provider_id\\\":\\\""+SupplyChainConfig.SERVICE_PROVIDER_ID+"\\\"}\"" +
                "," +
                "\"enable_pay_channels\":\"[{\\\"payChannelType\\\":\\\"MONEY_FUND\\\"}]\"" +
                "}";
        request.setBizContent(bizContent);
        AlipayFundAuthOrderFreezeResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 资金授权发码,暂时只提供余额宝
     */
    public static AlipayFundAuthOrderVoucherCreateResponse qrcodeFreeze(String amount, String out_order_no, String out_request_no,
                                                                        String payee_user_id, String title,String notify_url) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayFundAuthOrderVoucherCreateRequest request = new AlipayFundAuthOrderVoucherCreateRequest();
        Map<String, Object> parm = new HashMap<>();
        parm.put("out_order_no", out_order_no);
        parm.put("out_request_no", out_request_no);
        parm.put("order_title", title);
        parm.put("amount", amount);
        parm.put("payee_user_id", payee_user_id);
        parm.put("pay_timeout", "30m");
        parm.put("extra_param", "{\"category\":\"HOTEL\"}");
        parm.put("trans_currency", "USD");
        parm.put("settle_currency", "USD");
        parm.put("product_code", "PRE_AUTH");
        parm.put("enable_pay_channels", "[{\"payChannelType\":\"MONEY_FUND\"}]");
        request.setBizContent(JSON.toJSONString(parm));
        request.setNotifyUrl(notify_url);
        AlipayFundAuthOrderVoucherCreateResponse response = alipayClient.execute(request, null, APPAUTHTOKEN);
        return response;

    }

    //**
    // 资金冻结查询
    public static AlipayFundAuthOperationDetailQueryResponse queryFreeze(String out_order_no,String out_request_no) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayFundAuthOperationDetailQueryRequest request = new AlipayFundAuthOperationDetailQueryRequest();
        Map<String, Object> parm = new HashMap<>();
        parm.put("out_order_no", out_order_no);
        parm.put("out_request_no", out_request_no);
        System.out.println(JSON.toJSONString(parm));
        request.setBizContent(JSON.toJSONString(parm));
        AlipayFundAuthOperationDetailQueryResponse response = alipayClient.execute(request, null, APPAUTHTOKEN);
        return response;

    }

    //资金解冻
    public static void unFreeze(String auth_no, String amount, String remark, String out_request_no, Boolean bizComplete) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayFundAuthOrderUnfreezeRequest request = new AlipayFundAuthOrderUnfreezeRequest();
        Map<String, Object> parm = new HashMap<>();
        parm.put("auth_no", auth_no);
        parm.put("out_request_no", out_request_no);
        parm.put("remark", remark);
        parm.put("amount", amount);
        parm.put("extra_param", "{\"unfreezeBizInfo\": \"{\\\"bizComplete\\\":\\\"" + bizComplete + "\\\"}\"}");
        System.out.println(JSON.toJSONString(parm));
        request.setBizContent(JSON.toJSONString(parm));
        AlipayFundAuthOrderUnfreezeResponse response = alipayClient.execute(request, null, APPAUTHTOKEN);
        System.out.println(JSON.toJSONString(response));

    }

    //资金冻结转支付
    public static AlipayTradePayResponse tradePay(String auth_no, String amount, String body,String out_order_no,
                                String out_request_no, String seller_id,String freeze_alipay_id,
                                String storeId,String terminal_id,boolean complete) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        Map<String, Object> parm = new HashMap<>();
        parm.put("out_trade_no", out_order_no);
        parm.put("out_request_no", out_request_no);
        parm.put("auth_no", auth_no);
        parm.put("scene", "bar_code");
        parm.put("product_code", "PRE_AUTH");
        parm.put("total_amount", amount);
        parm.put("seller_id", seller_id);
        parm.put("buyer_id", freeze_alipay_id);
        parm.put("store_id", storeId);
        parm.put("terminal_id", terminal_id);
        parm.put("timeout_express", "30m");
        parm.put("subject", body);
        parm.put("body", body);
        parm.put("extra_param", "{\"sys_service_provider_id\":\"" + SupplyChainConfig.SERVICE_PROVIDER_ID + "\"}");
        if (complete) {
            parm.put("auth_confirm_mode", "COMPLETE");
        }//自动解冻时取值COMPLETE,不传该参数默认剩余资金不会自动解冻NOT_COMPLETE。
        System.out.println(JSON.toJSONString(parm));
        request.setBizContent(JSON.toJSONString(parm));
        request.setNotifyUrl(NOTIFY_URL);
        AlipayTradePayResponse response = alipayClient.execute(request, null, APPAUTHTOKEN);
        return response;

    }

    //资金冻结转支付后退款
    public static AlipayTradeRefundResponse tradeRefund(String refund_reason, String refund_amount, String out_order_no, String out_request_no) throws AlipayApiException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        Map<String, Object> parm = new HashMap<>();
        parm.put("out_trade_no", out_order_no);
        parm.put("out_request_no", out_request_no);
        parm.put("refund_reason", refund_reason);
        parm.put("refund_amount", refund_amount);
        System.out.println(JSON.toJSONString(parm));
        request.setBizContent(JSON.toJSONString(parm));
        AlipayTradeRefundResponse response = alipayClient.execute(request, null, APPAUTHTOKEN);
        return response;

    }


    public static AlipayClient getAlipayClient(){
        return new DefaultAlipayClient(SupplyChainConfig.GATEWAY, SupplyChainConfig.APP_ID, SupplyChainConfig.PRIVATE_KEY,
                "json", "UTF-8", SupplyChainConfig.PUBLIC_KEY, "RSA2");

    }



}

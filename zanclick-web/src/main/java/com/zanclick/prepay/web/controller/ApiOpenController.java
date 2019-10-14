package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.api.AsiaInfoHeader;
import com.zanclick.prepay.common.api.AsiaInfoUtil;
import com.zanclick.prepay.common.api.client.RestHttpClient;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.common.resolver.ApiRequestResolver;
import com.zanclick.prepay.common.utils.ApplicationContextProvider;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.web.exeption.DecryptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 * 中心网关
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "api_open_controller")
public class ApiOpenController {
    @Value("${h5.server}")
    private String h5Server;
    @Autowired
    private PayOrderService payOrderService;

    @GetMapping(value = "/verifyMerchant", produces = "application/json;charset=utf-8")
    public Response verifyMerchant(String appId, String cipherJson) {
        String method = "com.zanclick.verify.merchant";
        try {
            ResponseParam param = resolver(method, appId, cipherJson);
            if (param.isSuccess()) {
                return Response.ok(param.getData());
            }
            return Response.unsigned(param.getMessage());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        }
    }

    @GetMapping(value = "/notifyReset", produces = "application/json;charset=utf-8")
    public Response notifyReset(String orderNo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PayOrder order = payOrderService.queryByOrderNo(orderNo);
        if (order == null) {
            return Response.fail("订单号有误");
        }
        if (order.isPayed()) {
            AsiaInfoHeader header = AsiaInfoUtil.getHeader(order.getPhoneNumber());
            try {
                JSONObject object = new JSONObject();
                object.put("orderNo", order.getOrderNo());
                object.put("outOrderNo", order.getOutOrderNo());
                object.put("packageNo", order.getPackageNo());
                object.put("payTime", sdf.format(order.getFinishTime()));
                object.put("merchantNo", order.getMerchantNo());
                object.put("orderStatus", getOrderStatus(order.getState()));
                String result = RestHttpClient.post(header, object.toJSONString(), "commodity/freezenotify/v1.1.1");
                log.error("能力回调结果：{}", result);
            } catch (Exception e) {
                log.error("能力回调出错:{}", e);
                e.printStackTrace();
            }
        }
        return Response.ok("调用成功");
    }

    @GetMapping(value = "/createQr")
    public Response createQr(String appId, String cipherJson, HttpServletResponse response) {
        log.error("接收到支付创建请求：appId:{},cipherJson:{}",appId,cipherJson);
        String method = "com.zanclick.create.auth.prePay";
        String methodName = StringUtils.getMethodName(method);
        try {
//            ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(methodName);
            ResponseParam param = resolver(method,appId, cipherJson);
            if (!param.isSuccess()) {
                return Response.fail(param.getMessage());
            }
            JSONObject object = (JSONObject) param.getData();
            Integer state = object.getInteger("state");
            StringBuffer sb = new StringBuffer();
            if (state.equals(0)) {
                sb.append("/trade/create");
                sb.append("?appId=" + appId).append("&cipherJson=" + URLEncoder.encode(cipherJson, "utf-8"));
                sb.append("&qrCodeUrl=" + object.getString("qrCodeUrl")).append("&orderNo=" + object.getString("orderNo"));
                sb.append("&eachMoney=" + object.getString("eachMoney")).append("&totalMoney=" + object.getString("totalMoney"));
                sb.append("&num=" + object.getInteger("num")).append("&title=" + URLEncoder.encode(object.getString("title"), "utf-8"));
            } else if (state.equals(1)) {
                sb.append("/auth/success");
                sb.append("?orderNo=" + object.getString("orderNo")).append("&title=" + URLEncoder.encode(object.getString("title"), "utf-8"));
                sb.append("&money=" + object.getString("totalMoney"));
            } else if (state.equals(-1)) {
                sb.append("/auth/fail");
            }
            response.sendRedirect(h5Server + sb.toString());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        } catch (Exception e) {
            log.error("系统异常:{}", e);
            return Response.fail("系统繁忙，请稍后再试");
        }
        return Response.ok("");
    }


    @GetMapping(value = "/queryOrderList", produces = "application/json;charset=utf-8")
    public Response queryOrderList(String appId, String cipherJson) {
        try {
            String method = "com.zanclick.query.auth.order";
            ResponseParam param = resolver(method, appId, cipherJson);
            if (param.isSuccess()) {
                return Response.ok(param.getData());
            }
            return Response.fail(param.getMessage());
        } catch (DecryptException e) {
            return Response.fail("解密失败，请检查加密信息");
        }
    }

    private ResponseParam resolver(String method, String appId, String cipherJson) {
        try {
            String methodName = StringUtils.getMethodName(method);
            ApiRequestResolver resolver = (ApiRequestResolver) ApplicationContextProvider.getBean(methodName);
            String result = resolver.resolve(appId, cipherJson, null);
            ResponseParam param = JSONObject.parseObject(result, ResponseParam.class);
            return param;
        } catch (DecryptException e) {
            throw e;
        } catch (Exception e) {
            log.error("系统异常:{}", e);
            ResponseParam param = new ResponseParam();
            param.setFail();
            param.setMessage("系统繁忙,请稍后再试");
            return param;
        }
    }


    private String getOrderStatus(Integer state) {
        if (PayOrder.State.wait.getCode().equals(state)) {
            return "WAIT_PAY";
        } else if (PayOrder.State.payed.getCode().equals(state)) {
            return "TRADE_SUCCESS";
        }
        return "TRADE_CLOSED";
    }
}

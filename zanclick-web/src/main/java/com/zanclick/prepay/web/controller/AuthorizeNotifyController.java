package com.zanclick.prepay.web.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 预授权资金冻结亦不会掉
 *
 * @author duchong
 * @date 2019-7-10 10:39:55
 */
@Slf4j
@Api(description = "第三方放get请求调用接口")
@RestController("authorize_notify")
@RequestMapping(value = "/api/open/authorize")
public class AuthorizeNotifyController extends BaseController {

    @Autowired
    private AuthorizeOrderService authorizeOrderService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;
    @Autowired
    private PayOrderService payOrderService;

    @ApiOperation(value = "预授权回调地址")
    @PostMapping(value = "/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) {
        Map<String, String> params = getAllRequestParam(request);
        log.error("收到资金冻结成功通知:{}" + toJSONString(params));
        String status = params.get("status");
        String out_trade_no = params.get("out_order_no");
        AuthorizeOrder order = authorizeOrderService.queryByOrderNo(out_trade_no);
        if (order == null) {
            log.error("订单编号异常:{}", out_trade_no);
            return "failure";
        }
        if (!order.isUnPay()) {
            log.error("预授权订单状态不正确,请核对:{},{},{}", out_trade_no, status, order.getState());
            return "success";
        }
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(order.getConfigurationId());
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params,
                    configuration.getPublicKey(),
                    UTF_8,
                    configuration.getSignType());
        } catch (Exception e) {
            log.warn("预授权验签出错:{},{}", e, out_trade_no);
            return "failure";
        }
        if (!verify_result) {
            log.error("验签失败:{}", out_trade_no);
            return "failure";
        }
        try {
            if (status.equals("SUCCESS")) {
                order.setOperationId(params.get("operation_id"));
                order.setAuthNo(params.get("auth_no"));
                order.setBuyerId(params.get("payer_user_id"));
                order.setBuyerNo(params.get("payer_logon_id"));
                order.setState(AuthorizeOrder.State.payed.getCode());
                authorizeOrderService.handleAuthorizeOrder(order);
                payOrderService.handleSuccess(order.getOutTradeNo(),order.getAuthNo());
            }
        } catch (Exception e) {
            log.error("处理结果失败{}", e);
            return "failure";
        }
        return "success";
    }
}

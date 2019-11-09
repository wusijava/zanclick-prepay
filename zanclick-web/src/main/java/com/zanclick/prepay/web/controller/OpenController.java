package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 内部接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Api(description = "开放接口")
@Slf4j
@RestController(value = "open_controller")
@RequestMapping(value = "/api/open/")
public class OpenController {

    @Autowired
    private PayOrderService payOrderService;

    @ApiOperation(value = "通知补发")
    @GetMapping(value = "/resetNotify", produces = "application/json;charset=utf-8")
    public Response notifyReset(String orderNo) {
        PayOrder order = payOrderService.queryByOutTradeNo(orderNo);
        if (order == null) {
            return Response.fail("订单号有误");
        }
        if (order.isPayed()) {
            payOrderService.sendMessage(order);
        }
        return Response.ok("调用成功");
    }


    @ApiOperation(value = "导入商户信息")
    @RequestMapping(value = "batchImportMerchant", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchImportMerchant(MultipartFile file) {
        try {
            return Response.ok("导入商户成功");
        } catch (Exception e) {
            log.error("导入商户出错");
            return Response.fail("导入商户失败");
        }
    }

}

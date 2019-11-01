package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "open_controller")
@RequestMapping(value = "/api/open/")
public class OpenController {

    @Autowired
    private PayOrderService payOrderService;

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密后的摘要,每次请求后由服务端生成,通过Header返回", required = true, dataType = "String", paramType = "header")
    })
    @RequestMapping(value = "importMerchantInfo", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> importMerchantInfo(MultipartFile file){
        List<RegisterMerchant> merchantList = new ArrayList<>();
        return null;
    }


}

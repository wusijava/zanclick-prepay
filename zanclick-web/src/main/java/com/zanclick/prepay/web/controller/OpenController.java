package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
public class OpenController extends BaseController {

    @Autowired
    private PayOrderService payOrderService;

    @ApiOperation(value = "退款")
    @GetMapping(value = "/refund", produces = "application/json;charset=utf-8")
    public void refund(String outTradeNo,Integer type) throws IOException {
        String authorization = getAuthorization();
        if (authorization == null) {
            return ;
        }
        if (!verifyAuthorization(authorization,"prepay:prepayAdmin")){
            return ;
        }
        if (DataUtil.isEmpty(outTradeNo) || DataUtil.isEmpty(type)){
            return ;
        }
        try {
            payOrderService.refund(outTradeNo,type);
            getResponse().setCharacterEncoding("UTF-8");
            getResponse().setContentType("application/json;charset=utf-8");
            getResponse().getWriter().write("退款成功");
        }catch (Exception e){
            log.error("退款失败:{}",e);
            getResponse().setCharacterEncoding("UTF-8");
            getResponse().setContentType("application/json;charset=utf-8");
            getResponse().getWriter().write(e.getMessage());
        }
    }

    @ApiOperation(value = "通用excel下载接口")
    @GetMapping(value = "/downloadExcel/{key}")
    @ResponseBody
    public void downloadExcel(@PathVariable(value = "key") String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object object = RedisUtil.get(key);
        ExcelDto dto = object == null ? null : (ExcelDto) object;
        if (dto == null) {
            log.error("key已经过期:{}", key);
            return;
        }
        PoiUtil.batchExport(dto.getHeaders(), dto.getKeys(),dto.getObjectList(), request, response);
        RedisUtil.del(key);
    }

}

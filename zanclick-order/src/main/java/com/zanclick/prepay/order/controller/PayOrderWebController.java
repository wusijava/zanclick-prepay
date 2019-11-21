package com.zanclick.prepay.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.vo.PayOrderExcelList;
import com.zanclick.prepay.order.vo.PayOrderWebList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author duchong
 * @date 2019-11-9 14:51:47
 **/
@Api(description = "商户管理系统接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/pay/order")
public class PayOrderWebController extends BaseController {

    @Autowired
    private PayOrderService payOrderService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    @ApiOperation(value = "交易列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<PayOrderWebList>> list(PayOrderQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        if (user.getType().equals(1)){
            query.setUid(user.getStoreMarkCode());
        }else if (user.getType().equals(2)){
            query.setStoreMarkCode(user.getStoreMarkCode());
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<PayOrder> page = payOrderService.queryPage(query, pageable);
        List<PayOrderWebList> voList = new ArrayList<>();
        for (PayOrder order : page.getContent()) {
            voList.add(getListVo(order));
        }
        Page<PayOrderWebList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    @ApiOperation(value = "结算打款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/settle")
    @ResponseBody
    public Response settle(String outTradeNo) {
        if (DataUtil.isEmpty(outTradeNo)) {
            return Response.fail("缺少外部订单号");
        }
        PayOrder order = payOrderService.queryByOutTradeNo(outTradeNo);
        if (order == null) {
            return Response.fail("订单编号异常");
        }
        if (order.isSettled() || order.isSettleWait()) {
            log.error("订单状态异常:{},{}", outTradeNo, order.getDealStateDesc());
            return Response.fail("处理失败");
        }
        SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE, outTradeNo);
        payOrderService.syncQueryDealState(outTradeNo, order.getDealState());
        return Response.ok("处理成功");
    }

    @ApiOperation(value = "取单打款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/cancel")
    @ResponseBody
    public Response cancel(String outTradeNo) {
        if (DataUtil.isEmpty(outTradeNo)) {
            return Response.fail("缺少外部订单号");
        }
        PayOrder order = payOrderService.queryByOutTradeNo(outTradeNo);
        if (order == null) {
            return Response.fail("订单编号异常");
        }
        if (!order.isPayed() && !order.isRefund()) {
            log.error("订单状态不正确无法取消打款:{}", outTradeNo);
            return Response.fail("订单状态不正确无法取消打款");
        }
        if (!order.isSettleWait()){
            log.error("订单状态不正确无法取消打款:{}", outTradeNo);
            return Response.fail("订单状态不正确无法取消打款");
        }
        //TODO 这里取消打款功能暂时不在本期做
        return Response.ok("处理成功");
    }

    @ApiOperation(value = "退款")
    @PostMapping(value = "/refund")
    @ResponseBody
    public Response refund(String outTradeNo) {
        if (DataUtil.isEmpty(outTradeNo)) {
            return Response.fail("缺少外部订单号");
        }
        String reason = payOrderService.refund(outTradeNo, null);
        if (reason == null) {
            return Response.ok("退款成功");
        }
        return Response.fail(reason);
    }

    @ApiOperation(value = "导出交易信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping(value = "batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(PayOrderQuery query) {
        List<PayOrder> orderList = payOrderService.queryList(query);
        if (DataUtil.isEmpty(orderList)) {
            return Response.fail("没有数据");
        }
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        if (user.getType().equals(1)){
            query.setUid(user.getStoreMarkCode());
        }else if (user.getType().equals(2)){
            query.setStoreMarkCode(user.getStoreMarkCode());
        }
        List<PayOrderExcelList> orderExcelList = new ArrayList<>();
        for (PayOrder order : orderList) {
            PayOrderExcelList list = getExcelVo(order);
            if (DataUtil.isNotEmpty(list)) {
                orderExcelList.add(list);
            }
        }
        if (DataUtil.isEmpty(orderExcelList)) {
            return Response.fail("没有数据");
        }
        ExcelDto dto = new ExcelDto();
        dto.setHeaders(PayOrderExcelList.headers);
        dto.setKeys(PayOrderExcelList.keys);
        dto.setObjectList(parser(orderExcelList));
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, dto, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }


    /**
     * 获取显示Modal
     *
     * @param order
     * @return
     */
    private PayOrderWebList getListVo(PayOrder order) {
        PayOrderWebList vo = new PayOrderWebList();
        vo.setId(order.getId());
        vo.setMerchantNo(order.getMerchantNo());
        vo.setWayId(order.getWayId());
        vo.setCreateTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setFinishTime(order.getFinishTime() == null ? "" : DateUtil.formatDate(order.getFinishTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setAmount(order.getAmount());
        vo.setSettleAmount(order.getSettleAmount());
        vo.setNum(order.getNum());
        vo.setTitle(order.getTitle());
        vo.setStoreName(order.getStoreName());
        vo.setState(order.getState());
        vo.setReason(order.getReason());
        vo.setStateStr(order.getStateDesc());
        vo.setPhoneNumber(order.getPhoneNumber());
        vo.setOutOrderNo(order.getOutOrderNo());
        vo.setOutTradeNo(order.getOutTradeNo());
        vo.setAuthNo(order.getAuthNo());
        vo.setDealState(order.getDealState());
        vo.setDealStateStr(order.getDealStateDesc());
        vo.setSellerNo(order.getSellerNo());
        vo.setName(order.getName());
        vo.setRedPacketAmount(order.getRedPackAmount());
        vo.setRedPacketStateDesc(order.getRedPacketStateDesc());
        vo.setRedPacketSellerNo(order.getRedPackSellerNo());
        return vo;
    }

    private PayOrderExcelList getExcelVo(PayOrder order) {
        PayOrderExcelList vo = new PayOrderExcelList();
        vo.setWayId(order.getWayId());
        vo.setStoreName(order.getStoreName());
        vo.setOutOrderNo(order.getOutOrderNo());
        vo.setOutTradeNo(order.getOutTradeNo());
        vo.setAmount(order.getAmount());
        vo.setSettleAmount(order.getSettleAmount());
        vo.setNum(String.valueOf(order.getNum()));
        vo.setTitle(order.getTitle());
        vo.setPhoneNumber(order.getPhoneNumber());
        vo.setBuyerNo(order.getBuyerNo());
        vo.setSellerNo(order.getSellerNo());
        vo.setName(order.getName());
        vo.setStateStr(order.getStateDesc());
        vo.setCreateTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setFinishTime(order.getFinishTime() == null ? "" : DateUtil.formatDate(order.getFinishTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setProvince(order.getProvinceName());
        vo.setCity(order.getCityName());
        vo.setCounty(order.getDistrictName());
        return vo;
    }


    private List<JSONObject> parser(List<PayOrderExcelList> merchantList) {
        return JSONObject.parseArray(JSONObject.toJSONString(merchantList), JSONObject.class);
    }
}

package com.zanclick.prepay.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import com.zanclick.prepay.order.query.PayRefundOrderQuery;
import com.zanclick.prepay.order.service.PayRefundOrderService;
import com.zanclick.prepay.order.vo.PayRefundOrderExcelList;
import com.zanclick.prepay.order.vo.PayRefundOrderWebList;
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
@RequestMapping(value = "/api/web/pay/refund/order")
public class PayRefundOrderWebController extends BaseController {

    @Autowired
    private PayRefundOrderService payRefundOrderService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    @ApiOperation(value = "交易列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<PayRefundOrderWebList>> list(PayRefundOrderQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<PayRefundOrder> page = payRefundOrderService.queryPage(query, pageable);
        List<PayRefundOrderWebList> voList = new ArrayList<>();
        for (PayRefundOrder order : page.getContent()) {
            voList.add(getListVo(order));
        }
        Page<PayRefundOrderWebList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }


    @ApiOperation(value = "导出退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping(value = "/batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(PayRefundOrderQuery query) {
        try {
            List<PayRefundOrder> payRefundOrderList = payRefundOrderService.queryList(query);
            if (DataUtil.isEmpty(payRefundOrderList)) {
                return Response.fail("没有数据");
            }
            List<PayRefundOrderExcelList> payRefundOrderExcelList = new ArrayList<>();
            int index = 1;
            for (PayRefundOrder payRefundOrder : payRefundOrderList) {
                PayRefundOrderExcelList list = getExcelVo(payRefundOrder);
                if (DataUtil.isNotEmpty(list)) {
                    list.setIndex(index);
                    payRefundOrderExcelList.add(list);
                    index++;
                }
            }
            if (DataUtil.isEmpty(payRefundOrderExcelList)) {
                return Response.fail("没有数据");
            }
            ExcelDto dto = new ExcelDto();
            dto.setHeaders(PayRefundOrderExcelList.headers);
            dto.setKeys(PayRefundOrderExcelList.keys);
            dto.setObjectList(parser(payRefundOrderExcelList));
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            RedisUtil.set(key, dto, 1000 * 60 * 30L);
            String url = excelDownloadUrl + key;
            return Response.ok(url);

        } catch (Exception e) {
            log.error("导出退款信息出错:{}", e);
            return Response.fail("导出退款信息失败");
        }

    }

    @ApiOperation(value = "改动红包回款状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/changeRedPacketState")
    @ResponseBody
    public Response<String> changeRedPacketState(Long id) {
        PayRefundOrder order = payRefundOrderService.queryById(id);
        order.setRedPacketState(PayRefundOrder.RedPacketState.refund.getCode());
        payRefundOrderService.updateById(order);
        return Response.ok("回款成功");
    }


    @ApiOperation(value = "结算打款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/settle")
    @ResponseBody
    public Response settle(String outTradeNo) {
        try {
            payRefundOrderService.settle(outTradeNo);
        } catch (BizException e) {
            log.error("结清失败:{}", e);
            return Response.fail(e.getMessage());
        } catch (Exception e) {
            log.error("结清失败:{}", e);
            return Response.fail("结清失败");
        }
        return Response.ok("处理成功");
    }

    @ApiOperation(value = "改动垫资回款状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/changeRepaymentState")
    @ResponseBody
    public Response<String> changeRepaymentState(Long id) {
        PayRefundOrder order = payRefundOrderService.queryById(id);
        order.setRepaymentState(PayRefundOrder.RepaymentState.paid.getCode());
        payRefundOrderService.updateById(order);
        return Response.ok("回款成功");
    }

    /**
     * 获取显示Modal
     *
     * @param order
     * @return
     */
    private PayRefundOrderWebList getListVo(PayRefundOrder order) {
        PayRefundOrderWebList webList = new PayRefundOrderWebList();
        webList.setAmount(order.getAmount());
        webList.setAuthNo(order.getAuthNo());
        webList.setOutOrderNo(order.getOutOrderNo());
        webList.setOutTradeNo(order.getOutTradeNo());
        webList.setRedPacketAmount(order.getRedPacketAmount());
        webList.setRedPacketState(order.getRedPacketState());
        webList.setWayId(order.getWayId());
        webList.setState(order.getState());
        webList.setRepaymentState(order.getRepaymentState());
        webList.setId(order.getId());
        webList.setStateDesc(order.getStateDesc());
        webList.setRepaymentStateDesc(order.getRepaymentStateDesc());
        webList.setRedPacketStateDesc(order.getRedPacketDesc());
        webList.setCreateTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        webList.setFinishTime(order.getFinishTime() == null ? "" : DateUtil.formatDate(order.getFinishTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        webList.setSellerNo(order.getSellerNo());
        webList.setSellerName(order.getSellerName());
        webList.setDealTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        return webList;
    }

    private PayRefundOrderExcelList getExcelVo(PayRefundOrder order) {
        PayRefundOrderExcelList vo = new PayRefundOrderExcelList();
        vo.setOutTradeNo(order.getOutTradeNo());
        vo.setOutOrderNo(order.getOutOrderNo());
        vo.setAuthNo(order.getAuthNo());
        vo.setWayId(order.getWayId());
        vo.setCreateTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setFinishTime(order.getFinishTime() == null ? "" : DateUtil.formatDate(order.getFinishTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setDealTime(DateUtil.formatDate(order.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setAmount(order.getAmount());
        vo.setRedPacketAmount(order.getRedPacketAmount());
        vo.setStateDesc(order.getStateDesc());
        vo.setRedPacketStateDesc(order.getRedPacketDesc());
        vo.setRepaymentStateDesc(order.getRepaymentStateDesc());
        vo.setSellerName(order.getSellerName());
        vo.setSellerNo(order.getSellerNo());
        return vo;
    }

    private List<JSONObject> parser(List<PayRefundOrderExcelList> payRefundOrderExcelListList) {
        return JSONObject.parseArray(JSONObject.toJSONString(payRefundOrderExcelListList), JSONObject.class);
    }

}

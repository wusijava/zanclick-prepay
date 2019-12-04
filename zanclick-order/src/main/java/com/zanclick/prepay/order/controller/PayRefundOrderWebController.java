package com.zanclick.prepay.order.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.order.entity.PayRefundOrder;
import com.zanclick.prepay.order.query.PayRefundOrderQuery;
import com.zanclick.prepay.order.service.PayRefundOrderService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        return webList;
    }
}

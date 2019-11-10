package com.zanclick.prepay.order.controller;

import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.order.vo.PayOrderWebList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<PayOrderWebList>> list(PayOrderQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
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

    @ApiOperation(value = "导出交易信息")
    @RequestMapping(value = "batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(AuthorizeMerchantQuery query) {
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, null, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }


    /**
     * 获取显示Modal
     *
     * @param record
     * @return
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PayOrderWebList getListVo(PayOrder order) {
        PayOrderWebList vo = new PayOrderWebList();
        vo.setId(order.getId());
        vo.setMerchantNo(order.getMerchantNo());
        vo.setWayId(order.getWayId());
        vo.setCreateTime(sdf.format(order.getCreateTime()));
        vo.setFinishTime(order.getFinishTime() == null ? "" : sdf.format(order.getFinishTime()));
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
        vo.setDealState(order.getDealState());
        vo.setDealStateStr(order.getDealStateDesc());
        return vo;
    }

}

package com.zanclick.prepay.order.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.query.RedPacketQuery;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.order.vo.RedPacketList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/***
 * @author wusi
 *
 * */
@Api(description = "红包展示列表")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/red/packet")
public class RedPackWebController extends BaseController {
    @Autowired
    private RedPacketService RedPacketService;

    @ApiOperation(value = "红包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping("/list")
    public Response<Page<RedPacketList>> getPacketList(RedPacketQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<RedPacket> page = RedPacketService.queryPage(query, pageable);
        List<RedPacketList> voList = new ArrayList<>();
        for (RedPacket redPacket : page.getContent()) {
            voList.add(getListVo(redPacket));
        }
        Page<RedPacketList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    private RedPacketList getListVo(RedPacket redPacket) {
        RedPacketList vo = new RedPacketList();
        vo.setOutTradeNo(redPacket.getOutTradeNo());
        vo.setOutOrderNo(redPacket.getOutOrderNo());
        vo.setAmount(redPacket.getAmount());
        vo.setWayId(redPacket.getWayId());
        vo.setReceiveNo(redPacket.getReceiveNo());
        vo.setState(redPacket.getState());
        vo.setCreateTime(redPacket.getCreateTime() == null ? "" : DateUtil.formatDate(redPacket.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setStateDesc(redPacket.getStateDesc());
        vo.setReason(redPacket.getReason());
        return vo;
    }
}

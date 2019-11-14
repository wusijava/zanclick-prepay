package com.zanclick.prepay.order.controller;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.RedPacket;
import com.zanclick.prepay.order.query.RedPacketQuery;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.order.vo.RedPacketList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Description   :  红包列表controller
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/13$ 11:07$
 */
@Api(description = "红包展示列表")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/pay/redpacket")
public class RedPackController extends BaseController {
    @Autowired
   private RedPacketService RedPacketService;

    @ApiOperation(value="红包列表")
    @RequestMapping("/list")
    public Response<Page<RedPacketList>> getPacketList(RedPacketQuery query){
        System.out.println(query.getOrderNo());
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
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private RedPacketList getListVo(RedPacket redPacket) {
        RedPacketList vo = new RedPacketList();
        vo.setOrderNo(redPacket.getOrderNo());
        vo.setAmount(redPacket.getAmount());
        vo.setWayId(redPacket.getWayId());
        vo.setSellerNo(redPacket.getSellerNo());
        vo.setCreateTime(redPacket.getCreateTime());
        vo.setState(redPacket.getState());
        return vo;
    }
}

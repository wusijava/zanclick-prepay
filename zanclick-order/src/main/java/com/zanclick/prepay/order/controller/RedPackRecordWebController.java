package com.zanclick.prepay.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.query.RedPacketRecordQuery;
import com.zanclick.prepay.order.service.RedPacketRecordService;
import com.zanclick.prepay.order.vo.RedPacketExcelList;
import com.zanclick.prepay.order.vo.RedPacketList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/***
 * @author wusi
 *
 * */
@Api(description = "红包展示列表")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/red/packet")
public class RedPackRecordWebController extends BaseController {
    @Autowired
    private RedPacketRecordService RedPacketRecordService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    @ApiOperation(value = "红包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping("/list")
    public Response<Page<RedPacketList>> getPacketList(RedPacketRecordQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<RedPacketRecord> page = RedPacketRecordService.queryPage(query, pageable);
        List<RedPacketList> voList = new ArrayList<>();
        for (RedPacketRecord redPacketRecord : page.getContent()) {
            voList.add(getListVo(redPacketRecord));
        }
        Page<RedPacketList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    private RedPacketList getListVo(RedPacketRecord redPacketRecord) {
        RedPacketList vo = new RedPacketList();
        vo.setOutTradeNo(redPacketRecord.getOutTradeNo());
        vo.setOutOrderNo(redPacketRecord.getOutOrderNo());
        vo.setAmount(redPacketRecord.getAmount());
        vo.setWayId(redPacketRecord.getWayId());
        vo.setReceiveNo(redPacketRecord.getReceiveNo());
        vo.setState(redPacketRecord.getState());
        vo.setCreateTime(redPacketRecord.getCreateTime() == null ? "" : DateUtil.formatDate(redPacketRecord.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        vo.setStateDesc(redPacketRecord.getStateDesc());
        vo.setReason(redPacketRecord.getReason());
        return vo;
    }

    @ApiOperation(value = "导出红包信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping(value = "/batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(RedPacketRecordQuery query){
        try {
            List<RedPacketRecord> redPacketRecordList = RedPacketRecordService.queryList(query);
            if (DataUtil.isEmpty(redPacketRecordList)){
                return Response.fail("没有数据");
            }
            List<RedPacketExcelList> packetExcelList = new ArrayList<>();
            for(int i = 0; i< redPacketRecordList.size(); i++){
                RedPacketExcelList list = getExcelVo(redPacketRecordList.get(i));
                if (DataUtil.isNotEmpty(list)) {
                    list.setNo(String.valueOf(i+1));
                    packetExcelList.add(list);
                }
            }
            if (DataUtil.isEmpty(packetExcelList)){
                return Response.fail("没有数据");
            }
            ExcelDto dto = new ExcelDto();
            dto.setHeaders(RedPacketExcelList.headers);
            dto.setKeys(RedPacketExcelList.keys);
            dto.setObjectList(parser(packetExcelList));
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            RedisUtil.set(key, dto, 1000 * 60 * 30L);
            String url = excelDownloadUrl + key;
            return Response.ok(url);
        }catch (Exception e){
            log.error("导出红包信息出错:{}", e);
            return Response.fail("导出红包信息失败");
        }

    }

    private RedPacketExcelList getExcelVo(RedPacketRecord redPacketRecord){
        RedPacketExcelList packetExcelList = new RedPacketExcelList();
        packetExcelList.setOutTradeNo(redPacketRecord.getOutTradeNo());
        packetExcelList.setOutOrderNo(redPacketRecord.getOutOrderNo());
        packetExcelList.setAmount(redPacketRecord.getAmount());
        packetExcelList.setWayId(redPacketRecord.getWayId());
        packetExcelList.setReceiveNo(redPacketRecord.getReceiveNo());
        packetExcelList.setCreateTime(DateUtil.formatDate(redPacketRecord.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        packetExcelList.setState(redPacketRecord.getStateDesc());
        packetExcelList.setReason(redPacketRecord.getReason());
        return packetExcelList;
    }

    private List<JSONObject> parser(List<RedPacketExcelList> merchantList){
        return JSONObject.parseArray(JSONObject.toJSONString(merchantList), JSONObject.class);
    }

}

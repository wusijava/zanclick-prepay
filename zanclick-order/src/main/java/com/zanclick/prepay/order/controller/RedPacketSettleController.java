package com.zanclick.prepay.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.RedPackDetail;
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.entity.RedPacketTotal;
import com.zanclick.prepay.order.query.RedPacketQuery;
import com.zanclick.prepay.order.query.RedPacketTotalQuery;
import com.zanclick.prepay.order.service.RedPackDetailService;
import com.zanclick.prepay.order.service.RedPacketService;
import com.zanclick.prepay.order.service.RedPacketTotalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ Description   :  红包结算
 * @ Author        :  wusi
 * @ CreateDate    :  2019/12/12$ 16:26$
 */
@Api(description = "核算红包")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/redpacket/settle")
public class RedPacketSettleController {
    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private RedPacketTotalService redPacketTotalService;
    @Autowired
    private RedPackDetailService redPackDetailService;
    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;
    @RequestMapping("settlelist")
    @Transactional(rollbackFor = {SQLException.class})
    public Response<Page<RedPacketTotal>> getsettleList(RedPacketQuery redPacketQuery){
        //通过查询条件同步红包明细到redpackdetails表
        List<Map<String, Object>> settleDetail = redPacketService.getSettleDetail(redPacketQuery);
        List<RedPackDetail> detailList=new ArrayList<>();
        for(Map<String, Object> map:settleDetail){
            RedPackDetail redPackDetail=new RedPackDetail();
            redPackDetail.setOutOrderNo(map.get("out_order_no").toString());
            redPackDetail.setAmount( map.get("amount").toString());
            redPackDetail.setSettleAmount(map.get("settle_amount").toString());
            redPackDetail.setNum((Integer) map.get("num"));
            redPackDetail.setRedPackAmount(map.get("red_pack_amount").toString());
            redPackDetail.setSellerNo(map.get("seller_no").toString());
            detailList.add(redPackDetail);
        }
        if (detailList.size()!=0){
            redPackDetailService.insertBatch(detailList);
        }

        //同步分组红包数据到redpackettotal表 并分页返回前端
        RedPacketTotalQuery redPacketTotalQuery=new RedPacketTotalQuery();
        if (DataUtil.isEmpty(redPacketTotalQuery.getPage())) {
            redPacketTotalQuery.setPage(0);
        }
        if (DataUtil.isEmpty(redPacketTotalQuery.getLimit())) {
            redPacketTotalQuery.setLimit(10);
        }
        Pageable pageable = PageRequest.of(redPacketTotalQuery.getPage(), redPacketTotalQuery.getLimit());
        Page<RedPacketTotal> page=redPacketTotalService.queryPage(redPacketTotalQuery,pageable);
        List<Map<String, Object>> settleList = redPacketService.getSettleList(redPacketQuery);
        System.out.println(settleList.toString());
        List<RedPacketTotal> list=new ArrayList<>();
        for (Map<String, Object> redPacket:settleList) {
            RedPacketTotal redPacketTotal=new RedPacketTotal();
            System.out.println(redPacket.get("name"));
            System.out.println(redPacket.get("count").toString());
            redPacketTotal.setName(redPacket.get("name").toString());
            redPacketTotal.setSellerNo(redPacket.get("seller_no").toString());
            redPacketTotal.setType(((Integer)redPacket.get("type")));
            redPacketTotal.setUnsettleNum((Long)redPacket.get("count"));
            redPacketTotal.setUnsettleAmount((Double) redPacket.get("sum"));
            list.add(redPacketTotal);
        }
        if(list.size()==0){
            return  null;
        }else{
            redPacketTotalService.insertBatch(list);
            Page<RedPacketTotal> vopage= new PageImpl<>(list,pageable,page.getTotalElements());
            return  Response.ok(vopage);
        }

    }
    @ApiOperation(value = "导出红包明细信息")
    @RequestMapping(value = "batchExportredpacketdetails", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExportMerchant(AuthorizeMerchantQuery query) {




        ExcelDto dto = new ExcelDto();
        dto.setHeaders(RegisterMerchant.headers);
        dto.setKeys(RegisterMerchant.keys);
        dto.setObjectList(parser(null));
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, dto, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }
    private List<JSONObject> parser(List<RegisterMerchant> merchantList) {
        return JSONObject.parseArray(JSONObject.toJSONString(merchantList), JSONObject.class);
    }
}

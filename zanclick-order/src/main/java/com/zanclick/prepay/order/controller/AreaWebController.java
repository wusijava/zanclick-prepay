package com.zanclick.prepay.order.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.Area;
import com.zanclick.prepay.order.entity.RedPacketRecord;
import com.zanclick.prepay.order.service.AreaService;
import com.zanclick.prepay.order.service.RedPacketRecordService;
import com.zanclick.prepay.order.vo.AreaWebInfo;
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

/**
 * @Author panliang
 * @Date 2019/12/5 14:40
 * @Description //
 **/
@Api(description = "省市列表")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/pay/area")
public class AreaWebController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ApiOperation(value = "红包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping("/areaList")
    public Response<AreaWebInfo> getAreaList() {
        try{
            AreaWebInfo areaWebInfo = new AreaWebInfo();
            List<Area> provinceList = areaService.selectByLevel(1);
            List<AreaWebInfo.AreaInfo> provinceInfos = new ArrayList<>();
            if(DataUtil.isNotEmpty(provinceList)){
                for(Area province : provinceList){
                    AreaWebInfo.AreaInfo provinceInfo = new AreaWebInfo.AreaInfo();
                    provinceInfo.setCode(province.getCode());
                    provinceInfo.setName(province.getName());
                    provinceInfos.add(provinceInfo);
                }
            }

            List<Area> cityList = areaService.selectByLevel(2);
            List<AreaWebInfo.AreaInfo> cityInfos = new ArrayList<>();
            if(DataUtil.isNotEmpty(cityList)){
                for(Area city : cityList){
                    AreaWebInfo.AreaInfo cityInfo = new AreaWebInfo.AreaInfo();
                    cityInfo.setCode(city.getCode());
                    cityInfo.setName(city.getName());
                    cityInfos.add(cityInfo);
                }
            }
            areaWebInfo.setProvinceList(provinceInfos);
            areaWebInfo.setCityList(cityInfos);
            return Response.ok(areaWebInfo);
        }catch (Exception e){
            log.error("导出红包信息出错:{}", e);
            return Response.fail("导出红包信息失败");
        }
    }


}

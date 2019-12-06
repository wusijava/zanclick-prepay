package com.zanclick.prepay.order.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.order.entity.Area;
import com.zanclick.prepay.order.service.AreaService;
import com.zanclick.prepay.order.vo.AreaData;
import com.zanclick.prepay.order.vo.AreaWebInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/web/area")
public class AreaWebController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ApiOperation(value = "红包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping("/areaList")
    public Response<AreaWebInfo> getAreaList() {
        try {
            AreaWebInfo areaWebInfo = new AreaWebInfo();
            List<Area> provinceList = areaService.selectByLevel(1);
            List<AreaData> provinceInfos = new ArrayList<>();
            if (DataUtil.isNotEmpty(provinceList)) {
                for (Area province : provinceList) {
                    provinceInfos.add(getAreaDate(province));
                }
            }

            List<Area> cityList = areaService.selectByLevel(2);
            List<AreaData> cityInfos = new ArrayList<>();
            if (DataUtil.isNotEmpty(cityList)) {
                for (Area city : cityList) {
                    cityInfos.add(getAreaDate(city));
                }
            }
            areaWebInfo.setProvinceList(provinceInfos);
            areaWebInfo.setCityList(cityInfos);
            return Response.ok(areaWebInfo);
        } catch (Exception e) {
            log.error("导出红包信息出错:{}", e);
            return Response.fail("导出红包信息失败");
        }
    }

    @ApiOperation(value = "区域列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping("/getAreaListByLevelOrParentCode")
    public Response<List<AreaData>> getAreaListByLevelOrParentCode(Integer level, String parentCode) {
        try {
            if (DataUtil.isEmpty(level) || level <= 0 || level > 3) {
                log.error("level为空:{}", level);
                return Response.ok(null);
            }
            List<AreaData> areaDataList = new ArrayList<>();
            Area query = new Area();
            query.setLevel(level);
            query.setParentCode(parentCode);
            List<Area> areaList = areaService.queryList(query);
            if (!DataUtil.isEmpty(areaList)) {
                areaDataList.add(new AreaData("全部", ""));
                for (Area area : areaList) {
                    areaDataList.add(getAreaDate(area));
                }
            }
            return Response.ok(areaDataList);
        } catch (Exception e) {
            log.error("导出红包信息出错:{}", e);
            return Response.fail("导出红包信息失败");
        }
    }

    public AreaData getAreaDate(Area area) {
        AreaData data = new AreaData(area.getName(), area.getCode());
        return data;
    }


}

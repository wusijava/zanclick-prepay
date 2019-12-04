package com.zanclick.prepay.setmeal.controller.web;

import com.sun.org.apache.regexp.internal.RE;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.IpUtils;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.entity.SetMealLog;
import com.zanclick.prepay.setmeal.query.SetMealQuery;
import com.zanclick.prepay.setmeal.service.SetMealLogService;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.setmeal.vo.SetMealDetail;
import com.zanclick.prepay.setmeal.vo.SetMealWebList;
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

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Api(description = "套餐管理系统接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/set/meal")
public class SetMealController extends BaseController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealLogService setMealLogService;


    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    @ApiOperation(value = "套餐列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<SetMealWebList>> list(SetMealQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<SetMeal> page = setMealService.queryPage(query, pageable);
        List<SetMealWebList> voList = new ArrayList<>();
        for (SetMeal setMeal : page.getContent()) {
            voList.add(getListVo(setMeal));
        }
        Page<SetMealWebList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);

    }

    @ApiOperation(value = "改变红包上下架状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping(value = "/changeState")
    @ResponseBody
    public Response changeState(SetMeal updateSetMeal) {
        try {
            if (DataUtil.isEmpty(updateSetMeal.getId())){
                return Response.fail("缺少套餐编号");
            }
            //获取当前ip地址
            String ipAddress = IpUtils.getIpAddress(getRequest());
            //获取当前用户
            RequestContext.RequestUser user = RequestContext.getCurrentUser();
            SetMeal setMeal = setMealService.queryById(updateSetMeal.getId());
            if(DataUtil.isEmpty(setMeal)){
                return Response.fail("未找到此套餐");
            }
            Integer state = setMeal.getState();
            Integer uState = state == 0 ? 1 : 0;
            updateSetMeal.setState(uState);
            setMealService.updateById(updateSetMeal);
            //将记录插入记录表
            setMealLogService.insert(new SetMealLog(null,user.getId(),ipAddress,setMeal.getTitle(),DateUtil.getCurrentDate(),uState));
            return Response.ok("修改成功");
        }catch (Exception e){
            log.error("上下架出错:{}");
            return Response.fail("上下架出错",e);
        }

    }

    @ApiOperation(value = "修改套餐信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> update(SetMealDetail detail) {
        if (DataUtil.isEmpty(detail) || DataUtil.isEmpty(detail.getId())) {
            return Response.fail("修改套餐信息异常");
        }
        try {
            SetMeal meal = setMealDetail(detail);
            setMealService.updateById(meal);
        } catch (BizException e) {
            return Response.ok(e.getMessage());
        } catch (Exception e) {
            return Response.ok("修改失败");
        }
        return Response.ok("修改成功");
    }
    private SetMealWebList  getListVo(SetMeal setMeal){
        SetMealWebList webList = new SetMealWebList();
        webList.setId(setMeal.getId());
        webList.setPackageNo(setMeal.getPackageNo());
        webList.setTitle(setMeal.getTitle());
        webList.setTotalAmount(setMeal.getTotalAmount());
        webList.setSettleAmount(setMeal.getSettleAmount());
        webList.setNum(setMeal.getNum());
        webList.setAmount(setMeal.getAmount());
        webList.setState(setMeal.getState());
        webList.setStateStr(setMeal.getStateDesc());
        return  webList;
    }

    private SetMeal setMealDetail(SetMealDetail detail) {
        SetMeal meal = new SetMeal();
        meal.setId(detail.getId());
        meal.setTitle(detail.getTitle());
        meal.setSettleAmount(detail.getSettleAmount());
        meal.setAmount(detail.getEachAmount());
        return meal;
    }
}

package com.zanclick.prepay.setmeal.controller.web;

import com.sun.org.apache.regexp.internal.RE;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.IpUtils;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.entity.SetMealLog;
import com.zanclick.prepay.setmeal.query.SetMealQuery;
import com.zanclick.prepay.setmeal.service.SetMealLogService;
import com.zanclick.prepay.setmeal.service.SetMealService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private  HttpServletRequest request;
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
            String ipAddress = IpUtils.getIpAddress(request);
            //获取当前用户
            RequestContext.RequestUser user = RequestContext.getCurrentUser();
            //获取用户操作的套餐
            String userId = user.getId();
            SetMeal setMeal = setMealService.queryById(updateSetMeal.getId());
            Integer state = setMeal.getState();
            Integer uState = state == 0 ? 1 : 0;
            updateSetMeal.setState(uState);
            SetMeal setMeal1 = setMealService.updateById(updateSetMeal);
            //将记录插入记录表
            SetMealLog setMealLog = new SetMealLog();
            setMealLog.setCreateTime( DateUtil.getCurrentDate());
            setMealLog.setIp(ipAddress);
            setMealLog.setState(setMeal.getState());
            setMealLog.setTitle(setMeal.getTitle());
            setMealLog.setUserId(user.getId());
            setMealLogService.insert(new SetMealLog());
            return Response.ok("修改成功");
        }catch (Exception e){
            log.error("上下架出错:{}");
            return Response.fail("上下架出错",e);
        }

    }

    private SetMealWebList  getListVo(SetMeal setMeal){
        SetMealWebList webList = new SetMealWebList();
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
}

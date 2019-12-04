package com.zanclick.prepay.setmeal.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.query.SetMealQuery;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.setmeal.vo.SetMealDetail;
import com.zanclick.prepay.setmeal.vo.SetMealList;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duchong
 * @date 2019-11-9 14:51:47
 **/
@Api(description = "套餐管理系统接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/setMeal/")
public class SetMealWebController extends BaseController {

    @Autowired
    private SetMealService setMealService;

    @ApiOperation(value = "套餐列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),

            @ApiImplicitParam(name = "page", value = "页数", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "limit", value = "每页显示数量", required = true, dataType = "Integer", paramType = "form"),
    })
    @PostMapping(value = "list")
    @ResponseBody
    public Response<Page<SetMealList>> list(SetMealQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<SetMeal> page = setMealService.queryPage(query, pageable);
        List<SetMealList> voList = new ArrayList<>();
        for (SetMeal meal : page.getContent()) {
            voList.add(getListVo(meal));
        }
        Page<SetMealList> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    @ApiOperation(value = "套餐详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),

            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "form"),
    })
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public Response<SetMealDetail> detail(Long id) {
        SetMeal meal = setMealService.queryById(id);
        if (DataUtil.isEmpty(meal)) {
            return Response.fail("套餐详情异常");
        }
        return Response.ok(getMealDetail(meal));
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


    /**
     * 获取显示Modal
     *
     * @param meal
     * @return
     */
    private SetMealList getListVo(SetMeal meal) {
        SetMealList vo = new SetMealList();
        vo.setId(meal.getId());
        vo.setEachAmount(meal.getAmount());
        vo.setNum(meal.getNum());
        vo.setRedPacketAmount(meal.getRedPackAmount());
        vo.setRedPacketState(meal.getRedPackState());
        vo.setRedPacketStateStr(meal.getRedPacketStateStr());
        vo.setSettleAmount(meal.getSettleAmount());
        vo.setState(meal.getState());
        vo.setTitle(meal.getTitle());
        vo.setStateStr(meal.getStateStr());
        vo.setTotalAmount(meal.getTotalAmount());
        return vo;
    }


    /**
     * 获取详情
     *
     * @param meal
     */
    private SetMealDetail getMealDetail(SetMeal meal) {
        SetMealDetail vo = new SetMealDetail();
        vo.setId(meal.getId());
        vo.setEachAmount(meal.getAmount());
        vo.setNum(meal.getNum());
        vo.setRedPacketAmount(meal.getRedPackAmount());
        vo.setRedPacketState(meal.getRedPackState());
        vo.setRedPacketStateStr(meal.getRedPacketStateStr());
        vo.setSettleAmount(meal.getSettleAmount());
        vo.setState(meal.getState());
        vo.setTitle(meal.getTitle());
        vo.setStateStr(meal.getStateStr());
        vo.setTotalAmount(meal.getTotalAmount());
        return vo;
    }

    /**
     * 设置详情
     *
     * @param detail
     */
    private SetMeal setMealDetail(SetMealDetail detail) {
        SetMeal meal = new SetMeal();
        meal.setId(detail.getId());
        meal.setTitle(detail.getTitle());
        meal.setTotalAmount(detail.getTotalAmount());
        meal.setSettleAmount(detail.getSettleAmount());
        meal.setRedPackAmount(detail.getRedPacketAmount());
        meal.setAmount(detail.getEachAmount());
        meal.setRedPackState(detail.getRedPacketState());
        meal.setState(detail.getState());
        return meal;
    }
}

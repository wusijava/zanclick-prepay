package com.zanclick.prepay.authorize.controller.web;

import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import com.zanclick.prepay.authorize.query.RedPackBlacklistQuery;
import com.zanclick.prepay.authorize.service.RedPackBlacklistService;
import com.zanclick.prepay.authorize.vo.web.BlacklistWebInfo;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author panliang
 * @Date 2019/11/22 16:07
 * @Description //
 **/
@Slf4j
@RestController
@RequestMapping(value = "/api/web/authorize/redpackblacklist")
public class RedPackBlacklistWebCcntroller extends BaseController {

    @Autowired
    private RedPackBlacklistService redPackBlacklistService;

    @ApiOperation(value = "可领红包黑名单列表")
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<BlacklistWebInfo>> list(RedPackBlacklistQuery query) {
        try{
            if (DataUtil.isEmpty(query.getPage())) {
                query.setPage(0);
            }
            if (DataUtil.isEmpty(query.getLimit())) {
                query.setLimit(10);
            }
            Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
            Page<RedPackBlacklist> page = redPackBlacklistService.queryPage(query, pageable);
            List<BlacklistWebInfo> voList = new ArrayList<>();
            for (RedPackBlacklist blacklist : page.getContent()) {
                voList.add(getListVo(blacklist));
            }
            Page<BlacklistWebInfo> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
            return Response.ok(voPage);
        }catch (Exception e){
            log.error("获取黑名单列表失败:{}", e);
            return Response.fail("查询失败");
        }
    }

    @ApiOperation(value = "添加黑名单")
    @PostMapping(value = "/insertBlacklist")
    @ResponseBody
    public Response<RedPackBlacklist> insertBlacklist(RedPackBlacklist query){
        try {
            if (DataUtil.isEmpty(query) || DataUtil.isEmpty(query.getSellerNo()) || DataUtil.isEmpty(query.getName())){
                return Response.fail("参数有误");
            }
            Date date = new Date();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String nowDate = dateFormat.format(date);
            query.setCreateTime(nowDate);
            RedPackBlacklist oldBlacklist = redPackBlacklistService.querySellerNo(query.getSellerNo());
            if(DataUtil.isNotEmpty(oldBlacklist)){
                return Response.fail("收款账号已存在");
            }
            redPackBlacklistService.insert(query);
            return Response.ok("添加成功", query);
        }catch (Exception e){
            log.error("添加黑名单失败:{}", e);
            return Response.fail("添加失败");
        }
    }

    @ApiOperation(value = "修改黑名单信息")
    @RequestMapping(value = "/updateBlacklist", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> updateBlacklist(RedPackBlacklist updateBlacklist) {
        if (DataUtil.isEmpty(updateBlacklist) || DataUtil.isEmpty(updateBlacklist.getId()) || DataUtil.isEmpty(updateBlacklist.getSellerNo())) {
            return Response.fail("修改商户信息异常");
        }
        RedPackBlacklist oldBlacklist = redPackBlacklistService.querySellerNo(updateBlacklist.getSellerNo());
        if (DataUtil.isNotEmpty(oldBlacklist)) {
            return Response.fail("收款账号重复");
        }
        try {
            redPackBlacklistService.updateById(updateBlacklist);
        } catch (Exception e) {
            log.error("修改商户信息系统异常:{},{}", updateBlacklist, e);
            return Response.ok("修改失败");
        }
        return Response.ok("修改成功");
    }

    @ApiOperation(value = "删除黑名单信息")
    @RequestMapping(value = "/deleteBlacklist", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> deleteBlacklist(Long id) {
        try {
            if (DataUtil.isEmpty(id)) {
                return Response.fail("参数有误");
            }
            redPackBlacklistService.deleteById(id);
            return Response.ok("删除成功",null);
        } catch (Exception e) {
            log.error("修改黑名单信息异常:{}", id, e);
            return Response.fail("修改失败");
        }
    }


    /**
     * 获取显示Mod    *
     * @param record
     * @return
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private BlacklistWebInfo getListVo(RedPackBlacklist blacklist) {
        BlacklistWebInfo vo = new BlacklistWebInfo();
        vo.setId(blacklist.getId());
        vo.setCreateTime(blacklist.getCreateTime());
        vo.setName(blacklist.getName());
        vo.setSellerNo(blacklist.getSellerNo());
        return vo;
    }

}

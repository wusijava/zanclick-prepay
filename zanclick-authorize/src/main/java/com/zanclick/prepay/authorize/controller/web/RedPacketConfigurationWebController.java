package com.zanclick.prepay.authorize.controller.web;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.authorize.query.RedPacketConfigurationQuery;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationService;
import com.zanclick.prepay.authorize.vo.web.RedPacketConfigurationWebInfo;
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
 * @Date 2019/12/2 10:27
 * @Description //红包配置接口
 **/
@Slf4j
@RestController
@RequestMapping(value = "/api/web/authorize/redpackconfiguration")
public class RedPacketConfigurationWebController extends BaseController {

    @Autowired
    private RedPacketConfigurationService redPacketConfigurationService;

    @ApiOperation(value = "红包配置列表")
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<RedPacketConfigurationWebInfo>> list(RedPacketConfigurationQuery query) {
        try{
            if (DataUtil.isEmpty(query.getPage())) {
                query.setPage(0);
            }
            if (DataUtil.isEmpty(query.getLimit())) {
                query.setLimit(10);
            }
            Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
            Page<RedPacketConfiguration> page = redPacketConfigurationService.queryPage(query, pageable);
            List<RedPacketConfigurationWebInfo> voList = new ArrayList<>();
            for (RedPacketConfiguration configuration : page.getContent()) {
                voList.add(getListVo(configuration));
            }
            Page<RedPacketConfigurationWebInfo> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
            return Response.ok(voPage);
        }catch (Exception e){
            log.error("获取红包配置列表失败:{}", e);
            return Response.fail("查询失败");
        }
    }

    @ApiOperation(value = "添加不可领红包账号")
    @PostMapping(value = "/insertConfiguration")
    @ResponseBody
    public Response<RedPacketConfiguration> insertConfiguration(RedPacketConfiguration query){
        try {
            if (DataUtil.isEmpty(query) || DataUtil.isEmpty(query.getLevel()) || DataUtil.isEmpty(query.getName())
                    || DataUtil.isEmpty(query.getStatus())){
                return Response.fail("参数有误");
            }
            Date date = new Date();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDate = dateFormat.format(date);
            query.setCreateTime(nowDate);
            RedPacketConfiguration checkName = new RedPacketConfiguration();
            checkName.setName(query.getName());
            List<RedPacketConfiguration> configurationList = redPacketConfigurationService.queryList(checkName);
            if(DataUtil.isNotEmpty(configurationList)){
                return Response.fail("名称已存在");
            }
            redPacketConfigurationService.insert(query);
            return Response.ok("添加成功", query);
        }catch (Exception e){
            log.error("添加红包配置失败:{}", e);
            return Response.fail("添加失败");
        }
    }

    @ApiOperation(value = "删除红包配置信息")
    @RequestMapping(value = "/deleteConfiguration", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> deleteConfiguration(Long id) {
        try {
            if (DataUtil.isEmpty(id)) {
                return Response.fail("参数有误");
            }
            RedPacketConfiguration configuration = redPacketConfigurationService.queryById(id);
            if(DataUtil.isNotEmpty(configuration)){
                redPacketConfigurationService.deleteById(id);
            }
            return Response.ok("删除成功",null);
        } catch (Exception e) {
            log.error("删除红包配置信息异常:{}", id, e);
            return Response.fail("删除失败");
        }
    }

    private RedPacketConfigurationWebInfo getListVo(RedPacketConfiguration configuration) {
        RedPacketConfigurationWebInfo vo = new RedPacketConfigurationWebInfo();
        vo.setId(configuration.getId());
        vo.setCreateTime(configuration.getCreateTime());
        vo.setName(configuration.getName());
        vo.setLevel(configuration.getLevelDesc());
        vo.setStatus(configuration.getStatusDesc());
        return vo;
    }
}

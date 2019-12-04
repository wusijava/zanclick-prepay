package com.zanclick.prepay.authorize.controller.web;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.authorize.entity.RedPacketConfigurationRecord;
import com.zanclick.prepay.authorize.query.RedPacketConfigurationQuery;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationRecordService;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationService;
import com.zanclick.prepay.authorize.vo.web.RedPacketConfigurationWebInfo;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.IpUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    private RedPacketConfigurationRecordService recordService;

    @ApiOperation(value = "红包配置列表")
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<RedPacketConfigurationWebInfo>> list(HttpServletRequest req, HttpServletResponse resp, RedPacketConfigurationQuery query) {
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
                    || DataUtil.isEmpty(query.getStatus()) || DataUtil.isEmpty(query.getNameCode()) || DataUtil.isEmpty(query.getAmountInfo())){
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

    @ApiOperation(value = "获取红包配置信息")
    @RequestMapping(value = "/getConfiguration", method = RequestMethod.POST)
    @ResponseBody
    public Response getConfiguration(HttpServletRequest req, HttpServletResponse resp, Long id) {
        if (DataUtil.isEmpty(id)) {
            return Response.fail("获取红包配置信息失败");
        }
        try {
            RedPacketConfiguration configuration = redPacketConfigurationService.queryById(id);
            if (DataUtil.isEmpty(configuration)) {
                return Response.fail("获取红包配置信息失败");
            }
//            RedPacketConfigurationWebInfo configurationWebInfo = getListVo(configuration);
            return Response.ok(configuration);
        } catch (Exception e) {
            log.error("获取红包配置信息失败:{}", e);
            return Response.fail("获取红包配置信息失败");
        }
    }

    @ApiOperation(value = "修改红包配置")
    @RequestMapping(value = "/updateConfiguration", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> updateConfiguration(HttpServletRequest req, HttpServletResponse resp, RedPacketConfiguration updateConfig) {
        try {
            if (DataUtil.isEmpty(updateConfig) || DataUtil.isEmpty(updateConfig.getId()) || DataUtil.isEmpty(updateConfig.getName())
                    || DataUtil.isEmpty(updateConfig.getNameCode()) || DataUtil.isEmpty(updateConfig.getLevel())
                    || DataUtil.isEmpty(updateConfig.getStatus()) || DataUtil.isEmpty(updateConfig.getAmountInfo())) {
                return Response.fail("参数有误");
            }
//            RedPacketConfiguration checkName = new RedPacketConfiguration();
//            checkName.setName(updateConfig.getName());
//            List<RedPacketConfiguration> configurationList = redPacketConfigurationService.queryList(checkName);
//            if (DataUtil.isNotEmpty(configurationList)) {
//                return Response.fail("账号重复");
//            }

            redPacketConfigurationService.updateById(updateConfig);
            //修改日志记录
            RedPacketConfigurationRecord record = getRecordVo(updateConfig);
            Date date = new Date();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDate = dateFormat.format(date);
            record.setCreateTime(nowDate);
            record.setAddress(IpUtils.getIpAddress(req));
            RequestContext.RequestUser user = RequestContext.getCurrentUser();
            record.setUserId(user.getUid());
            recordService.insert(record);
            return Response.ok("修改成功");
        } catch (Exception e) {
            log.error("修改红包配置信息异常:{},{}", updateConfig, e);
            return Response.fail("修改失败");
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

    private RedPacketConfigurationRecord getRecordVo(RedPacketConfiguration configuration) {
        RedPacketConfigurationRecord vo = new RedPacketConfigurationRecord();
//        vo.setId(configuration.getId());
        vo.setName(configuration.getName());
        vo.setNameCode(configuration.getNameCode());
        vo.setLevel(configuration.getLevel());
        vo.setStatus(configuration.getStatus());
        vo.setAmountInfo(configuration.getAmountInfo());
        return vo;
    }

}

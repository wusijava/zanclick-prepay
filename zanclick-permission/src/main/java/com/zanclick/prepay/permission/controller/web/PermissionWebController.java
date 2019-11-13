package com.zanclick.prepay.permission.controller.web;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.permission.service.RoleService;
import com.zanclick.prepay.permission.vo.HomeMenuList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理
 *
 * @author duchong
 * @date 2019-8-3 10:50:06
 */
@Api(description = "权限web接口")
@RestController(value = "auth_permission_controller")
@RequestMapping(value = "/api/web/permission")
public class PermissionWebController extends BaseController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),
    })
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Response<List<HomeMenuList>> list() {
        long startTime = System.currentTimeMillis();
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        List<HomeMenuList> list = roleService.findPermissionByType(user.getType());
        long endTime = System.currentTimeMillis();
        System.err.println(endTime-startTime);
        return Response.ok(list);
    }
}

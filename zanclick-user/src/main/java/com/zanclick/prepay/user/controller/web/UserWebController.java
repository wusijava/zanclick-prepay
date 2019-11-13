package com.zanclick.prepay.user.controller.web;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.query.UserQuery;
import com.zanclick.prepay.user.service.UserService;
import com.zanclick.prepay.user.vo.web.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户
 *
 * @author duchong
 * @date 2019-8-3 10:50:06
 */
@Api(description = "用户web接口")
@RestController(value = "user_web_controller")
@RequestMapping(value = "/api/web/user")
public class UserWebController extends BaseController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密参数", required = true, dataType = "String", paramType = "header"),

            @ApiImplicitParam(name = "page", value = "页数", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "limit", value = "每页显示数量", required = true, dataType = "Integer", paramType = "form"),
    })
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Response list(UserQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<User> page = userService.queryPage(query, pageable);
        return Response.ok(page);
    }

    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户登录名", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<UserInfo> login(String username, String password) {
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        UserInfo info = new UserInfo();
        info.setMobile(user.getMobile());
        info.setUsername(user.getUsername());
        info.setUid(user.getUid());
        return Response.ok(info);
    }

}

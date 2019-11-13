package com.zanclick.prepay.user.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.service.UserService;
import com.zanclick.prepay.user.vo.web.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录
 *
 * @author duchong
 * @date 2019-8-3 10:50:06
 */
@Api(description = "登录注册相关接口")
@RestController
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

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


    @ApiOperation(value = "验证手机号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "电话号码", required = true, dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/verifyMobile", method = RequestMethod.POST)
    @ResponseBody
    public Response verifyMobile(String mobile) {
        if (DataUtil.isEmpty(mobile)) {
            return Response.fail("缺少手机号");
        }
        if (!StringUtils.isPhone(mobile)) {
            return Response.fail("手机号格式错误");
        }
        User user = userService.findByUsername(mobile);
        if (DataUtil.isNotEmpty(user)) {
            return Response.fail("该手机号已注册");
        }
        return Response.ok("验证成功");
    }

}

package com.zanclick.prepay.permission.controller.web;

import com.zanclick.prepay.common.base.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理
 *
 * @author duchong
 * @date 2019-8-3 10:50:06
 */
@Api(description = "权限web接口")
@RestController(value = "auth_web_controller")
@RequestMapping(value = "/api/web/auth")
public class AuthWebController extends BaseController {


}

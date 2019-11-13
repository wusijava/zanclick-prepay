package com.zanclick.prepay.permission.secure.resolver.impl;

import com.zanclick.prepay.permission.secure.resolver.UserPermissionResolver;
import com.zanclick.prepay.permission.service.RoleService;
import com.zanclick.prepay.permission.vo.HomeMenuList;
import com.zanclick.prepay.permission.vo.LoginUser;
import com.zanclick.prepay.permission.vo.UsernamePasswordToken;
import com.zanclick.prepay.common.utils.PassWordUtil;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tuchuan
 * @description
 * @date 2019/3/15 10:51
 */
@Slf4j
@Component(value = "webUserPermissionResolver")
public class MyUserPermissionResolver extends UserPermissionResolver {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public List<HomeMenuList> getUserPermissions(Integer type) {
        return roleService.findPermissionByType(type);
    }

    @Override
    public LoginUser getLoginUser(UsernamePasswordToken token) {
        User user = userService.findByUsername(token.getUsername());
        return getLoginUser(user);
    }

    @Override
    public LoginUser getLoginUser(String userId) {
        User user = userService.queryById(Long.valueOf(userId));
        return getLoginUser(user);
    }

    @Override
    protected Boolean comparePassWord(UsernamePasswordToken token, LoginUser loginUser) {
        String loginPass = PassWordUtil.generatePasswordSha1WithSalt(token.getPassword(), loginUser.getSalt());
        return loginPass.equals(loginUser.getPassword());
    }

    @Override
    public void checkPermission(LoginUser user, String path) {
    }

    /**
     * 获取登录用户
     *
     * @param user
     * @return
     */
    private LoginUser getLoginUser(User user) {
        if (user != null) {
            LoginUser loginUser = new LoginUser();
            loginUser.setId(String.valueOf(user.getId()));
            loginUser.setMobile(user.getMobile());
            loginUser.setNickName(user.getNickName());
            loginUser.setPassword(user.getPassword());
            loginUser.setType(user.getType());
            loginUser.setUsername(user.getUsername());
            loginUser.setUid(user.getUid());
            loginUser.setSalt(user.getSalt());
            return loginUser;
        } else {
            return null;
        }
    }
}

package com.zanclick.prepay.auth.secure.resolver.impl;

import com.zanclick.prepay.auth.entity.LoginUser;
import com.zanclick.prepay.auth.entity.UsernamePasswordToken;
import com.zanclick.prepay.auth.secure.resolver.UserPermissionResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author tuchuan
 * @description
 * @date 2019/3/15 10:51
 */
@Slf4j
@Component(value = "webUserPermissionResolver")
public class MyUserPermissionResolver extends UserPermissionResolver {
    @Override
    public Set<String> getUserPermissions(LoginUser user) {
        return null;
    }

    @Override
    public LoginUser getLoginUser(UsernamePasswordToken token) {
        return null;
    }

    @Override
    public LoginUser getLoginUser(String userId) {
        return null;
    }

//    @Autowired
//    private UserService userService;
//
//    @Override
//    public Set<String> getUserPermissions(LoginUser user) {
//        return null;
//    }
//
//    @Override
//    public LoginUser getLoginUser(UsernamePasswordToken token) {
//        User user = userService.findByUsername(token.getUsername());
//        return getLoginUser(user);
//    }
//
//    @Override
//    public LoginUser getLoginUser(String userId) {
//        User user = userService.queryById(Long.valueOf(userId));
//        return getLoginUser(user);
//    }
//
//    @Override
//    protected Boolean comparePassWord(UsernamePasswordToken token, LoginUser loginUser) {
//        String loginPass = PassWordUtil.generatePasswordSha1WithSalt(token.getPassword(), loginUser.getSalt());
//        return loginPass.equals(loginUser.getPassword());
//    }
//
//    @Override
//    public void checkPermission(LoginUser user, String path) {
//    }
//
//    /**
//     * 获取登录用户
//     *
//     * @param user
//     * @return
//     */
//    private LoginUser getLoginUser(User user) {
//        if (user != null) {
//            LoginUser loginUser = new LoginUser();
//            loginUser.setId(String.valueOf(user.getId()));
//            loginUser.setMobile(user.getMobile());
//            loginUser.setNickName(user.getNickName());
//            loginUser.setPassword(user.getPassword());
//            loginUser.setType(user.getType());
//            loginUser.setUsername(user.getUsername());
//            loginUser.setUid(user.getUid());
//            loginUser.setSalt(user.getSalt());
//            return loginUser;
//        } else {
//            return null;
//        }
//    }
}

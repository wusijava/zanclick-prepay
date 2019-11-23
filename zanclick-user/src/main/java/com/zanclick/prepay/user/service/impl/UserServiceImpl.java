package com.zanclick.prepay.user.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.PassWordUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.user.entity.StoreMark;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.mapper.UserMapper;
import com.zanclick.prepay.user.query.UserQuery;
import com.zanclick.prepay.user.service.StoreMarkService;
import com.zanclick.prepay.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Administrator
 * @date 2019-08-03 10:42:46
 **/
@Slf4j
@Service
public class UserServiceImpl extends BaseMybatisServiceImpl<User, Long> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StoreMarkService storeMarkService;


    @Override
    protected BaseMapper<User, Long> getBaseMapper() {
        return userMapper;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByUid(String uid) {
        return userMapper.findByUid(uid);
    }

    @Override
    public String changePassword(Long userId, String salt, String password, String oldPassword, String newPassword) {
        String oldPass = PassWordUtil.generatePasswordSha1WithSalt(oldPassword, salt);
        if (!oldPass.equals(password)) {
            return "原密码错误";
        }
        String newPass = PassWordUtil.generatePasswordSha1WithSalt(newPassword, salt);
        UserQuery query = new UserQuery();
        query.setId(userId);
        query.setPassword(newPass);
        query.setPwd(newPassword);
        this.updateById(query);
        return null;
    }

    @Override
    public String changePassword(Long userId, String salt, String newPassword) {
        String newPass = PassWordUtil.generatePasswordSha1WithSalt(newPassword, salt);
        UserQuery query = new UserQuery();
        query.setId(userId);
        query.setPassword(newPass);
        this.updateById(query);
        return null;
    }


    /**
     * 创建用户
     *
     * @param aliPayLoginNo
     * @param merchantName
     * @param storeName
     * @param wayId
     * @param mobile
     */
    @Override
    public User createUser(String aliPayLoginNo, String merchantName,String storeName,String wayId,String mobile) {
        User user = userMapper.findByUsername(wayId);
        if (user != null){
            log.error("用户登录名重复:{}",wayId);
            return user;
        }
        StoreMark mark = storeMarkService.createStoreMark(aliPayLoginNo,merchantName);
        User query = new User();
        String pwd = StringUtils.createRandom(false,8);
        String salt = PassWordUtil.generateSalt();
        query.setCreateTime(new Date());
        query.setMobile(mobile);
        query.setUsername(wayId);
        query.setType(User.Type.USER.getCode());
        query.setUid(StringUtils.getMerchantNo());
        query.setSalt(salt);
        query.setState(User.State.OPEN.getCode());
        query.setPassword(PassWordUtil.generatePasswordSha1WithSalt(pwd,salt));
        query.setStoreMarkCode(mark.getCode());
        query.setNickName(storeName);
        query.setPwd(pwd);
        this.insert(query);
        return query;
    }
}

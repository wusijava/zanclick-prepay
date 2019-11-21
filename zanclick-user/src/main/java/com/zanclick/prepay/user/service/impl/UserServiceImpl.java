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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Administrator
 * @date 2019-08-03 10:42:46
 **/
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
    public UserQuery createUser(String aliPayLoginNo, String merchantName,String storeName,String wayId,String mobile) {
        StoreMark mark = storeMarkService.createStoreMark(aliPayLoginNo,merchantName);
        UserQuery user = new UserQuery();
        String salt = PassWordUtil.generateSalt();
        user.setCreateTime(new Date());
        user.setMobile(mobile);
        user.setUsername(wayId);
        user.setType(User.Type.USER.getCode());
        user.setUid(StringUtils.getMerchantNo());
        user.setSalt(salt);
        user.setState(User.State.OPEN.getCode());
        user.setPwd(StringUtils.createRandom(false,8));
        user.setPassword(PassWordUtil.generatePasswordSha1WithSalt(user.getPwd(),salt));
        user.setStoreMarkCode(mark.getCode());
        user.setNickName(storeName);
        getBaseMapper().insert(user);
        return user;
    }
}

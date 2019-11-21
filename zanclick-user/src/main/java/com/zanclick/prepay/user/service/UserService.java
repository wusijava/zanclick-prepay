package com.zanclick.prepay.user.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.query.UserQuery;

/**
 * @author Administrator
 * @date 2019-08-03 10:42:46
 **/
public interface UserService extends BaseService<User, Long> {

    /**
     * 根据用户名查找
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 根据用户id查询
     *
     * @param uid
     * @return
     */
    User findByUid(String uid);


    /**
     * 修改密码
     *
     * @param userId
     * @param salt
     * @param password
     * @param oldPassword
     * @param newPassword
     * @@return
     */
    String changePassword(Long userId, String salt, String password, String oldPassword, String newPassword);

    /**
     * 修改密码
     *
     * @param userId
     * @param salt
     * @param newPassword
     * @@return
     */
    String changePassword(Long userId, String salt, String newPassword);


    /**
     * 创建用户
     *
     * @param aliPayLoginNo
     * @param merchantName
     * @param storeName
     * @param wayId
     * @param mobile
     */
    UserQuery createUser(String aliPayLoginNo, String merchantName, String storeName, String wayId, String mobile);

}

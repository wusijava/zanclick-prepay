package com.zanclick.prepay.user.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @date 2019-08-03 10:42:46
 **/
@Mapper
public interface UserMapper extends BaseMapper<User, Long> {

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
}

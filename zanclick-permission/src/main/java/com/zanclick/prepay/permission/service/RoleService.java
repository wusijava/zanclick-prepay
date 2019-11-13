package com.zanclick.prepay.permission.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.Role;
import com.zanclick.prepay.permission.vo.HomeMenuList;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:50:57
 **/
public interface RoleService extends BaseService<Role, Long> {

    /**
     * 查询该角色的权限
     *
     * @param type
     */
    List<HomeMenuList> findPermissionByType(Integer type);

}

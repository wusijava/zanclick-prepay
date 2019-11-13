package com.zanclick.prepay.permission.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.RoleMenu;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:51:12
 **/
public interface RoleMenuService extends BaseService<RoleMenu,Long> {

    /**
     * 查询开启的菜单
     * @param type
     * */
    List<HomeMenu> queryByType(Integer type);

}

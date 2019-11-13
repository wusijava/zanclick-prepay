package com.zanclick.prepay.permission.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.permission.entity.Menu;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:41:06
 **/
public interface MenuService extends BaseService<Menu, Long> {


    /**
     * 查询开启的菜单
     *
     * @param homeCode
     * @return
     */
    List<Menu> queryOpenByHomeCode(String homeCode);


    /**
     * 查询开启的菜单
     *
     * @param code
     * @return
     */
    Menu queryOpenByCode(String code);


}

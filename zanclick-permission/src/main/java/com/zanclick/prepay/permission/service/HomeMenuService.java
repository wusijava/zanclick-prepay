package com.zanclick.prepay.permission.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.Menu;

/**
 * @author Administrator
 * @date 2019-11-13 14:42:24
 **/
public interface HomeMenuService extends BaseService<HomeMenu, Long> {

    /**
     * 查询开启的菜单
     *
     * @param code
     * @return
     */
    HomeMenu queryOpenByCode(String code);

}

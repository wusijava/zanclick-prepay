package com.zanclick.prepay.permission.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.Menu;
import com.zanclick.prepay.permission.entity.RoleMenu;
import com.zanclick.prepay.permission.mapper.RoleMenuMapper;
import com.zanclick.prepay.permission.query.RoleMenuQuery;
import com.zanclick.prepay.permission.service.HomeMenuService;
import com.zanclick.prepay.permission.service.MenuService;
import com.zanclick.prepay.permission.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Administrator
 * @date 2019-11-13 14:51:12
 **/
@Service
public class RoleMenuServiceImpl extends BaseMybatisServiceImpl<RoleMenu, Long> implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private HomeMenuService homeMenuService;
    @Autowired
    private MenuService menuService;


    @Override
    protected BaseMapper<RoleMenu, Long> getBaseMapper() {
        return roleMenuMapper;
    }

    @Override
    public List<HomeMenu> queryByType(Integer type) {
        RoleMenuQuery query = new RoleMenuQuery();
        query.setType(type);
        List<RoleMenu> menus = this.queryList(query);
        SortedMap<String, HomeMenu> homeMenuMap = new TreeMap<>();
        for (RoleMenu roleMenu : menus) {
            Menu menu = menuService.queryOpenByCode(roleMenu.getMenuCode());
            if (menu == null) {
                continue;
            }
            HomeMenu homeMenu = homeMenuMap.get(menu.getHomeCode());
            if (homeMenu == null) {
                homeMenu = homeMenuService.queryOpenByCode(roleMenu.getHomeMenuCode());
                if (homeMenu == null) {
                    continue;
                }
            }
            List<Menu> menuList = homeMenu.getSubmenus();
            if (menuList == null) {
                menuList = new ArrayList<>();
            }
            menuList.add(menu);
            homeMenu.setSubmenus(menuList);
            homeMenuMap.put(homeMenu.getCode(), homeMenu);
        }
        return getHomeList(homeMenuMap);
    }


    /**
     * map转list
     *
     * @param homeMenuMap
     */
    private List<HomeMenu> getHomeList(SortedMap<String, HomeMenu> homeMenuMap) {
        List<HomeMenu> menus = new ArrayList<>();
        for (String menu : homeMenuMap.keySet()) {
            menus.add(homeMenuMap.get(menu));
        }
        return menus;
    }

}

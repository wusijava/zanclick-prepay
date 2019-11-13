package com.zanclick.prepay.permission.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.Menu;
import com.zanclick.prepay.permission.entity.Role;
import com.zanclick.prepay.permission.mapper.RoleMapper;
import com.zanclick.prepay.permission.service.RoleMenuService;
import com.zanclick.prepay.permission.service.RoleService;
import com.zanclick.prepay.permission.vo.HomeMenuList;
import com.zanclick.prepay.permission.vo.MenuList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:50:57
 **/
@Service
public class RoleServiceImpl extends BaseMybatisServiceImpl<Role, Long> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    protected BaseMapper<Role, Long> getBaseMapper() {
        return roleMapper;
    }

    @Override
    public List<HomeMenuList> findPermissionByType(Integer type) {
        List<HomeMenuList> menuList = new ArrayList<>();
        List<HomeMenu> homeMenuList = roleMenuService.queryByType(type);
        for (HomeMenu menu:homeMenuList){
            HomeMenuList list = new HomeMenuList();
            list.setTitle(menu.getTitle());
            list.setIcon(menu.getIcon());
            list.setName(menu.getName());
            if (DataUtil.isNotEmpty(menu.getSubmenus())){
                List<MenuList> submenus = new ArrayList<>();
                for (Menu m:menu.getSubmenus()){
                    MenuList ml = new MenuList();
                    ml.setTitle(m.getTitle());
                    ml.setIcon(m.getIcon());
                    ml.setName(m.getName());
                    ml.setPath(m.getPath());
                    ml.setComponent(m.getComponent());
                    ml.setType(m.getType());
                    submenus.add(ml);
                }
                list.setSubmenus(submenus);
            }
            menuList.add(list);
        }
        return menuList;
    }
}

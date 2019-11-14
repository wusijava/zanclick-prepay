package com.zanclick.prepay.permission.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.permission.entity.Menu;
import com.zanclick.prepay.permission.mapper.MenuMapper;
import com.zanclick.prepay.permission.query.MenuQuery;
import com.zanclick.prepay.permission.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:41:06
 **/
@Service
public class MenuServiceImpl extends BaseMybatisServiceImpl<Menu,Long> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    protected BaseMapper<Menu, Long> getBaseMapper() {
        return menuMapper;
    }

    @Override
    public List<Menu> queryOpenByHomeCode(String homeCode) {
        MenuQuery query = new MenuQuery();
        query.setHomeCode(homeCode);
        query.setState(Menu.State.open.getCode());
        return this.queryList(query);
    }

    @Override
    public Menu queryOpenByCode(String code) {
        MenuQuery query = new MenuQuery();
        query.setCode(code);
        query.setState(Menu.State.open.getCode());
        List<Menu> menuList = this.queryList(query);
        return menuList == null || menuList.size() == 0 ? null : menuList.get(0);
    }

}

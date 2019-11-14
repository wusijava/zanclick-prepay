package com.zanclick.prepay.permission.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.permission.entity.HomeMenu;
import com.zanclick.prepay.permission.entity.Menu;
import com.zanclick.prepay.permission.mapper.HomeMenuMapper;
import com.zanclick.prepay.permission.query.HomeMenuQuery;
import com.zanclick.prepay.permission.service.HomeMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @date 2019-11-13 14:42:24
 **/
@Service
public class HomeMenuServiceImpl extends BaseMybatisServiceImpl<HomeMenu,Long> implements HomeMenuService {

    @Autowired
    private HomeMenuMapper homeMenuMapper;


    @Override
    protected BaseMapper<HomeMenu, Long> getBaseMapper() {
        return homeMenuMapper;
    }

    @Override
    public HomeMenu queryOpenByCode(String code) {
        HomeMenuQuery query = new HomeMenuQuery();
        query.setCode(code);
        query.setState(Menu.State.open.getCode());
        List<HomeMenu> menuList = this.queryList(query);
        return menuList == null || menuList.size() == 0 ? null : menuList.get(0);
    }
}

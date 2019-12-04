package com.zanclick.prepay.order.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.order.entity.Area;
import com.zanclick.prepay.order.mapper.AreaMapper;
import com.zanclick.prepay.order.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2019-12-02 16:24:45
 **/
@Service
public class AreaServiceImpl extends BaseMybatisServiceImpl<Area,Long> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;


    @Override
    protected BaseMapper<Area, Long> getBaseMapper() {
        return areaMapper;
    }


    @Override
    public Area selectByName(Area name) {
        return areaMapper.selectByName(name);
    }
}

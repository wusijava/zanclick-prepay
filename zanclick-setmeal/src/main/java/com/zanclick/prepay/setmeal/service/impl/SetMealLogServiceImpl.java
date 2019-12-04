package com.zanclick.prepay.setmeal.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.setmeal.entity.SetMealLog;
import com.zanclick.prepay.setmeal.mapper.SetMealLogMapper;
import com.zanclick.prepay.setmeal.service.SetMealLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-12-03 16:12:28
 **/
@Service
public class SetMealLogServiceImpl extends BaseMybatisServiceImpl<SetMealLog,Long> implements SetMealLogService {

    @Autowired
    private SetMealLogMapper setMealLogMapper;


    @Override
    protected BaseMapper<SetMealLog, Long> getBaseMapper() {
        return setMealLogMapper;
    }
}

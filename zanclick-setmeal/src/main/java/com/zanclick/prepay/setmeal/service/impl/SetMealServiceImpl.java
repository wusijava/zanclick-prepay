package com.zanclick.prepay.setmeal.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.mapper.SetMealMapper;
import com.zanclick.prepay.setmeal.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2019-12-03 15:59:44
 **/
@Service
public class SetMealServiceImpl extends BaseMybatisServiceImpl<SetMeal,Long> implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    protected BaseMapper<SetMeal, Long> getBaseMapper() {
        return setMealMapper;
    }

    @Override
    public SetMeal queryByPackageNo(String packageNo) {
        return setMealMapper.selectByPackageNo(packageNo);
    }

    @Override
    public void unshelveSetMealByAppId(String appId) {
        if(DataUtil.isNotEmpty(appId)) {
            setMealMapper.unshelveSetMealByAppId(appId);
        }
    }

}

package com.zanclick.prepay.setmeal.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.setmeal.entity.SetMeal;

/**
 * @author Administrator
 * @date 2019-12-03 15:59:44
 **/
public interface SetMealService extends BaseService<SetMeal,Long> {

    /**
     * 根据套餐编号查询
     *
     * @param packageNo
     * @return
     */
    SetMeal queryByPackageNo(String packageNo);

    void unshelveSetMealByAppId(String appId);
}

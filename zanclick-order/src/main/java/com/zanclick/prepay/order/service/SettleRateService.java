package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.SettleRate;

/**
 * @author Administrator
 * @date 2019-10-31 14:21:08
 **/
public interface SettleRateService extends BaseService<SettleRate,Long> {


    /**
     * 根据appId查询
     *
     * @param appId
     * @param num
     * @return
     */
    SettleRate queryByAppId(String appId,Integer num);


}

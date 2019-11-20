package com.zanclick.prepay.user.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.user.entity.StoreMark;

/**
 * @author Administrator
 * @date 2019-11-20 15:52:13
 **/
public interface StoreMarkService extends BaseService<StoreMark, Long> {


    /**
     * 根据支付宝账号编码
     *
     * @param aliPayLoginNo
     */
    StoreMark queryByAliPayLoginNo(String aliPayLoginNo);


    /**
     * 创建门店标识
     *
     * @param aliPayLoginNo
     * @param name
     */
    StoreMark createStoreMark(String aliPayLoginNo, String name);

}

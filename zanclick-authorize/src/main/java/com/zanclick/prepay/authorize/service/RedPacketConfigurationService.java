package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.common.base.service.BaseService;

public interface RedPacketConfigurationService extends BaseService<RedPacketConfiguration, Long> {


    /**
     * 查询红包相关
     *
     * @param aliPayLoginNo
     * @param cityCode
     * @param provinceCode
     * @param amount
     * @param num
     * @return
     */
    String queryRedPacketAmount(String aliPayLoginNo, String cityCode, String provinceCode, String amount, Integer num);

}

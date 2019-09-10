package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;

/**
 * 直付通平台费率
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeMerchantMapper extends BaseMapper<AuthorizeMerchant, Long> {

    /**
     * 根据商户ID查询
     *
     * @param merchantNo
     * @return
     */
    AuthorizeMerchant selectByMerchantNo(String merchantNo);

}

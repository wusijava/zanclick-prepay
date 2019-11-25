package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;

import java.util.List;

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

    /**
     * 根据支付宝收款行号查询
     *
     * @param sellerNo
     * @return
     */
    AuthorizeMerchant selectByAliPayLoginNo(String sellerNo);

    /**
     * 根据支付宝收款账号查询List
     *
     * @param sellerNo
     * @return
     */
    List<AuthorizeMerchant> selectBySellerNo(String sellerNo);

    /**
     * 通过收款账号更新可领红包状态
     * @param merchant
     */
    void updateBySellerNo(AuthorizeMerchant merchant);

}

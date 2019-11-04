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

    /**
     * 根据支付宝收款行号查询
     *
     * @param sellerNo
     * @return
     */
    AuthorizeMerchant selectByAliPayLoginNo(String sellerNo);
    /**
     * 导入商户信息列表
     *
     * @param
     * @return
     */
    void importMerchantList( String wayId,String storeProvince,String storeCity,String storeCounty,String storeNo,String storeName,String storeSubjectCertNo,String storeSubjectName,String contactName,
                             String contactPhone,String name,String sellerId);

}

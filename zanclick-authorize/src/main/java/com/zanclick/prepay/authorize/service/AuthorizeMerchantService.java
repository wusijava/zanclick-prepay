package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.base.service.BaseService;

/**
 * 预授权商户
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeMerchantService extends BaseService<AuthorizeMerchant, Long> {


    /**
     * 创建预授权商户
     *
     * @param dto
     * @return
     */
    void createMerchant(RegisterMerchant dto);


    /**
     * 查询商户
     *
     * @param merchantNo
     * @return
     */
    AuthorizeMerchant queryMerchant(String merchantNo);

    /**
     * 根据支付宝收款行号查询
     *
     * @param sellerNo
     * @return
     */
    AuthorizeMerchant queryByAliPayLoginNo(String sellerNo);
}

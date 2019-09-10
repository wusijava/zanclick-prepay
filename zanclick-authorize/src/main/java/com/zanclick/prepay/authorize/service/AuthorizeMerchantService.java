package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.dto.RegisterMerchant;
import com.zanclick.prepay.authorize.dto.MerchantResult;
import com.zanclick.prepay.authorize.dto.MerchantUpdateDTO;

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
     * 修改预授权商户
     *
     * @param dto
     * @return
     */
    MerchantResult updateMerchant(MerchantUpdateDTO dto);
}

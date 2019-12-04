package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.base.service.BaseService;

import java.util.List;

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
    AuthorizeMerchant createMerchant(RegisterMerchant dto);


    /**
     * 创建预授权商户
     *
     * @param merchant
     * @return
     */
    void updateMerchant(AuthorizeMerchant merchant);

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


    /**
     * 查询重复的商户
     *
     * @param merchantNo
     * @return
     */
    Boolean queryRepeatMerchant(String merchantNo);

    /**
     * 商户领红包的权限开关
     */
    void isReceive(Long id);

    /**
     * 根据支付宝收款账号查询List
     *
     * @param sellerNo
     * @return
     */
    List<AuthorizeMerchant> queryBySellerNo(String sellerNo);

    /**
     * 通过收款账号更新可领红包状态
     * @param merchant
     */
    void updateBySellerNo(AuthorizeMerchant merchant);
}

package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.dto.RegisterMerchant;
import com.zanclick.prepay.authorize.dto.MerchantResult;
import com.zanclick.prepay.authorize.dto.MerchantUpdateDTO;
import com.zanclick.prepay.authorize.mapper.AuthorizeMerchantMapper;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.supplychain.util.SupplyChainUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 预授权商户
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Slf4j
@Service
public class AuthorizeMerchantServiceImpl extends BaseMybatisServiceImpl<AuthorizeMerchant, Long> implements AuthorizeMerchantService {

    @Autowired
    private AuthorizeMerchantMapper authorizeMerchantMapper;

    @Override
    protected BaseMapper<AuthorizeMerchant, Long> getBaseMapper() {
        return authorizeMerchantMapper;
    }

    @Override
    public void createMerchant(RegisterMerchant register) {
        String repeat = queryWaitOrSuccessMerchant(register.getMerchantNo());
        if (repeat != null) {
            throw new BizException(repeat);
        }
        AuthorizeMerchant merchant = createAuthorizeMerchant(register);
        createSupplier(merchant);
        if (merchant.isFail()) {
            throw new BizException(merchant.getReason());
        }
    }

    @Override
    public AuthorizeMerchant queryMerchant(String merchantNo) {
        return authorizeMerchantMapper.selectByMerchantNo(merchantNo);
    }

    @Override
    public MerchantResult updateMerchant(MerchantUpdateDTO dto) {
        MerchantResult result = new MerchantResult();
        return result;
    }


    /**
     * 查询是否有正在审核中的商户
     *
     * @param dto
     * @return
     */
    private AuthorizeMerchant createAuthorizeMerchant(RegisterMerchant dto) {
        AuthorizeMerchant merchant = new AuthorizeMerchant();
        merchant.setAppId(dto.getAppId());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setCreateTime(new Date());
        merchant.setName(dto.getName());
        merchant.setOperatorName(dto.getOperatorName());
        merchant.setStoreSubjectName(dto.getStoreSubjectName());
        merchant.setStoreSubjectCertNo(dto.getStoreSubjectCertNo());
        merchant.setStoreNo(dto.getStoreNo());
        merchant.setStoreName(dto.getStoreName());
        merchant.setStoreProvince(dto.getStoreProvince());
        merchant.setStoreCity(dto.getStoreCity());
        merchant.setStoreCounty(dto.getStoreCounty());
        merchant.setSellerNo(dto.getSellerNo());
        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
        this.insert(merchant);
        return merchant;
    }

    /**
     * 查询是否有正在审核中的商户
     *
     * @param dto
     * @return
     */
    private AuthorizeMerchant updateAuthorizeMerchant(MerchantUpdateDTO dto) {
        AuthorizeMerchant merchant = queryMerchant(dto.getMerchantNo());
        if (DataUtil.isNotEmpty(dto.getName())) {
            merchant.setName(dto.getName());
        }
        if (DataUtil.isNotEmpty(dto.getContactName())) {
            merchant.setContactName(dto.getContactName());
        }
        if (DataUtil.isNotEmpty(dto.getContactPhone())) {
            merchant.setContactPhone(dto.getContactPhone());
        }
        if (DataUtil.isNotEmpty(dto.getSellerId())) {
            merchant.setSellerId(dto.getSellerId());
        }
        if (DataUtil.isNotEmpty(dto.getSellerNo())) {
            merchant.setSellerNo(dto.getSellerNo());
        }
        return merchant;
    }

    private static final String OPRATORCHANNEL = "中国移动";

    /**
     * 签约商户
     *
     * @param merchant
     * @return （原因）没有返回，则为签约成功
     */
    private void createSupplier(AuthorizeMerchant merchant) {
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(
                merchant.getSellerNo(),
                merchant.getName(),
                merchant.getContactName(),
                merchant.getContactPhone(),
                null,
                OPRATORCHANNEL,
                merchant.getStoreNo(),
                merchant.getStoreName(),
                merchant.getStoreSubjectName(),
                merchant.getStoreSubjectCertNo(),
                merchant.getStoreProvince(),
                merchant.getStoreCity(),
                merchant.getStoreCounty()
        );
        if (response.isSuccess()) {
            merchant.setState(AuthorizeMerchant.State.success.getCode());
            merchant.setSupplierNo(response.getSupplierNo());
        } else {
            merchant.setReason(response.getSubMsg());
            merchant.setState(AuthorizeMerchant.State.failed.getCode());
        }
        merchant.setFinishTime(new Date());
        this.updateById(merchant);
    }

    /**
     * 签约商户
     *
     * @param merchant
     * @return （原因）没有返回，则为签约成功
     */
    private String updateSupplier(AuthorizeMerchant merchant) {
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(
                merchant.getSellerNo(),
                merchant.getName(),
                merchant.getContactName(),
                merchant.getContactPhone(),
                null,
                merchant.getOperatorName(),
                merchant.getStoreNo(),
                merchant.getStoreName(),
                merchant.getStoreSubjectName(),
                merchant.getStoreSubjectCertNo(),
                merchant.getStoreProvince(),
                merchant.getStoreCity(),
                merchant.getStoreCounty()
        );
        if (response.isSuccess()) {
            merchant.setSupplierNo(response.getSupplierNo());
            this.updateById(merchant);
        }
        return response.getSubMsg();
    }

    /**
     * 查询是否有正在审核中的商户
     *
     * @param merchantNo
     * @return
     */
    private String queryWaitOrSuccessMerchant(String merchantNo) {
        AuthorizeMerchant merchant = authorizeMerchantMapper.selectByMerchantNo(merchantNo);
        if (merchant != null && merchant.getState() != null) {
            if (merchant.getState().equals(AuthorizeMerchant.State.waiting.getCode())) {
                return "资料已提交，请耐心等待审核";
            }
            if (merchant.getState().equals(AuthorizeMerchant.State.success.getCode())) {
                return "请勿重复提交";
            }
        }
        return null;
    }
}

package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.mapper.AuthorizeMerchantMapper;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @Override
    protected BaseMapper<AuthorizeMerchant, Long> getBaseMapper() {
        return authorizeMerchantMapper;
    }

    @Override
    public AuthorizeMerchant createMerchant(RegisterMerchant register) {
        if (queryRepeatMerchant(register.getMerchantNo())) {
            log.error("商户创建渠道编码重复:{}",register.getWayId());
            throw new BizException("渠道编码重复");
        }
        String reason = register.check();
        if (reason != null){
            log.error("商户创建失败:{}",register.getWayId(),reason);
            throw new BizException(reason);
        }
        AuthorizeMerchant merchant = createAuthorizeMerchant(register);
        createMerchant(merchant);
        return merchant;
    }

    @Override
    public void updateMerchant(AuthorizeMerchant merchant) {
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
        if (response.isSuccess()) {
            merchant.setSupplierNo(response.getSupplierNo());
            this.updateById(merchant);
            return;
        }
        log.error("修改商户信息异常:{}",response.getSubMsg());
        throw new BizException("修改商户信息异常");
    }

    /**
     * 创建商户
     *
     * @param merchant
     */
    private void createMerchant(AuthorizeMerchant merchant) {
        AuthorizeMerchant oldMerchant = this.queryByAliPayLoginNo(merchant.getSellerNo());
        if (oldMerchant != null && oldMerchant.getState() != null) {
            if (oldMerchant.getState().equals(AuthorizeMerchant.State.success.getCode())) {
                if (oldMerchant.getStoreNo() != null && oldMerchant.getStoreNo().equals(merchant.getStoreNo())) {
                    throw new BizException("门店编号重复");
                }
                log.error("商户创建成功:{}",merchant.getWayId());
                merchant.setSupplierNo(oldMerchant.getSupplierNo());
                merchant.setState(AuthorizeMerchant.State.success.getCode());
                merchant.setFinishTime(new Date());
                this.updateById(merchant);
                return;
            }
        }
        createSupplier(merchant);
        if (merchant.isFail()) {
            log.error("商户创建失败:{},{}",merchant.getWayId(),merchant.getReason());
            throw new BizException(merchant.getReason());
        }
    }

    @Override
    public AuthorizeMerchant queryMerchant(String merchantNo) {
        return authorizeMerchantMapper.selectByMerchantNo(merchantNo);
    }

    @Override
    public AuthorizeMerchant queryByAliPayLoginNo(String sellerNo) {
        return authorizeMerchantMapper.selectByAliPayLoginNo(sellerNo);
    }


    @Override
    public RegisterMerchant getRegisterMerchant(AuthorizeMerchant dto) {
        RegisterMerchant merchant = new RegisterMerchant();
        merchant.setAppId(dto.getAppId());
        merchant.setWayId(dto.getWayId());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setName(dto.getName());
        merchant.setOperatorName(dto.getOperatorName());
        merchant.setStoreSubjectName(dto.getStoreSubjectName());
        merchant.setStoreSubjectCertNo(dto.getStoreSubjectCertNo());
        merchant.setStoreNo(dto.getStoreNo());
        merchant.setStoreName(dto.getStoreName());
        merchant.setStoreProvince(dto.getStoreProvince());
        merchant.setStoreCity(dto.getStoreCity());
        merchant.setStoreCounty(dto.getStoreCounty());
        merchant.setStoreProvinceCode(dto.getStoreProvinceCode());
        merchant.setStoreCityCode(dto.getStoreCityCode());
        merchant.setStoreCountyCode(dto.getStoreCountyCode());
        merchant.setSellerNo(dto.getSellerNo());
        merchant.setState(dto.getStateDesc());
        merchant.setReason(dto.getReason());
        merchant.setCreateTime(DateUtil.formatDate(dto.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        return merchant;
    }

    /**
     * 查询是否有重复的商户
     *
     * @@param merchantNo
     */
    @Override
    public Boolean queryRepeatMerchant(String merchantNo) {
        AuthorizeMerchant queryMerchant = new AuthorizeMerchant();
        queryMerchant.setMerchantNo(merchantNo);
        List<AuthorizeMerchant> merchantList = this.queryList(queryMerchant);
        if (DataUtil.isNotEmpty(merchantList)) {
            for (AuthorizeMerchant merchant : merchantList) {
                if (!merchant.isFail()) {
                    return true;
                }
            }
        }
        return false;
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
        merchant.setWayId(dto.getWayId());
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
        merchant.setStoreProvinceCode(dto.getStoreProvinceCode());
        merchant.setStoreCityCode(dto.getStoreCityCode());
        merchant.setStoreCountyCode(dto.getStoreCountyCode());
        merchant.setSellerNo(dto.getSellerNo());
        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
        merchant.setRedPackState(AuthorizeMerchant.RedPackState.open.getCode());
        this.insert(merchant);
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
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant), configuration);
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
     * 创建商户
     *
     * @param merchant
     * @return
     */
    private SuppilerCreate create(AuthorizeMerchant merchant) {
        SuppilerCreate create = new SuppilerCreate();
        create.setStoreNo(merchant.getSellerNo());
        create.setSellerName(merchant.getName());
        create.setRcvContactEmail(null);
        create.setRcvLoginId(merchant.getSellerNo());
        create.setRcvContactName(merchant.getContactName());
        create.setRcvContactPhone(merchant.getContactPhone());
        create.setOperatorName(OPRATORCHANNEL);
        create.setStoreNo(merchant.getStoreNo());
        create.setStoreName(merchant.getStoreName());
        create.setStoreSubjectName(merchant.getStoreSubjectName());
        create.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        create.setStoreProvince(merchant.getStoreProvince());
        create.setStoreCity(merchant.getStoreCity());
        create.setStoreCounty(merchant.getStoreCounty());
        return create;
    }

}

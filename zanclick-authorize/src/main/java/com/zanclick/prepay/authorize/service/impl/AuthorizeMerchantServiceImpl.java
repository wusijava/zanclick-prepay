package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import com.zanclick.prepay.authorize.mapper.AuthorizeMerchantMapper;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.service.RedPackBlacklistService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
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

    @Autowired
    private RedPackBlacklistService redPackBlacklistService;

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
            //校验商户可领红包权限开关 add panliang 2019.11.25
            this.isReceive(merchant.getId());
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
     * 商户领红包的权限开关
     * @param id
     */
    @Override
    public void isReceive(Long id) {
        if(DataUtil.isEmpty(id)){
            return;
        }
        AuthorizeMerchant merchant = authorizeMerchantMapper.selectById(id);
        if(DataUtil.isEmpty(merchant)){
            return;
        }
        if(merchant.getState()!=1){
            return;
        }
        //取出商户收款账号
        String sellerNo = merchant.getSellerNo();
        //通过收款账号查询红包收款黑名单(若能查到,则商户默认不能领取红包)
        RedPackBlacklist blacklist = redPackBlacklistService.querySellerNo(sellerNo);
        if(DataUtil.isEmpty(blacklist)){
            //没有查到,说明商户可以领红包,
            if(merchant.getRedPackState() != 1){
                //若收红包的状态不是可领取,则修改收款账号为可领取 1
                AuthorizeMerchant update = new AuthorizeMerchant();
                update.setId(merchant.getId());
                update.setRedPackState(AuthorizeMerchant.RedPackState.open.getCode());
                authorizeMerchantMapper.updateById(update);
                return;
            }
        }else{
            //能查到,说明商户不能领取红包
            if(merchant.getRedPackState() == 1){
                //若收红包的状态是可领取,则修改为不可领取 0
                AuthorizeMerchant update = new AuthorizeMerchant();
                update.setId(merchant.getId());
                update.setRedPackState(AuthorizeMerchant.RedPackState.closed.getCode());
                authorizeMerchantMapper.updateById(update);
                return;
            }
        }
    }


    /**
     * 根据支付宝收款账号查询List
     * @param sellerNo
     * @return
     */
    @Override
    public List<AuthorizeMerchant> queryBySellerNo(String sellerNo) {
        return authorizeMerchantMapper.selectBySellerNo(sellerNo);
    }

    /**
     * 通过收款账号更新可领红包状态
     * @param merchant
     */
    @Override
    public void updateBySellerNo(AuthorizeMerchant merchant) {
        authorizeMerchantMapper.updateBySellerNo(merchant);
        return;
//        log.error("修改商户领红包状态异常:{}", merchant.toString());
//        throw new BizException("修改商户领红包状态失败");
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
        if(DataUtil.isNotEmpty(dto.getSellerNo())){
            RedPackBlacklist blacklist = redPackBlacklistService.querySellerNo(dto.getSellerNo());
            if(DataUtil.isEmpty(blacklist)){
                merchant.setRedPackState(AuthorizeMerchant.RedPackState.open.getCode());
            }else{
                merchant.setRedPackState(AuthorizeMerchant.RedPackState.closed.getCode());
            }
        }else{
            merchant.setRedPackState(AuthorizeMerchant.RedPackState.open.getCode());
        }
        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
//        merchant.setRedPackState(AuthorizeMerchant.RedPackState.open.getCode());
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

    @Override
    public AuthorizeMerchant queryLastOneByWayId(String wayId) {
        return authorizeMerchantMapper.queryLastOneByWayId(wayId);
    }
}

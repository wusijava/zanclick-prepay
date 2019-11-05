package com.zanclick.prepay.web;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author lvlu
 * @date 2019-07-06 14:32
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class ZftTest {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @Test
    public void sss(){
       /* List<RegisterMerchant> merchantList = ExcelUtil.readExcel("E:\\excel\\20191102.xls","Sheet1");
        for (RegisterMerchant merchant:merchantList){
            createAuthorizeMerchant(merchant);
        }*/
    }

    @Test
    public void ss(){
        AuthorizeMerchant query = new AuthorizeMerchant();
        query.setState(0);
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        for (AuthorizeMerchant merchant:merchantList){
            try {
                createSupplier(merchant);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
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
        authorizeMerchantService.insert(merchant);
        return merchant;
    }


    /**
     * 签约商户
     *
     * @param merchant
     * @return （原因）没有返回，则为签约成功
     */
    private void createSupplier(AuthorizeMerchant merchant) {
        System.err.println(merchant.getId());
        AuthorizeMerchant oldMerchant = authorizeMerchantService.queryByAliPayLoginNo(merchant.getSellerNo());
        if (oldMerchant != null ) {
            if (oldMerchant.getState().equals(AuthorizeMerchant.State.success.getCode())){
                merchant.setSupplierNo(oldMerchant.getSupplierNo());
                merchant.setState(AuthorizeMerchant.State.success.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return ;
            }else if (oldMerchant.getState().equals(AuthorizeMerchant.State.failed.getCode())){
                merchant.setReason(oldMerchant.getReason());
                merchant.setState(AuthorizeMerchant.State.failed.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return ;
            }
        }
        if ( oldMerchant == null || (oldMerchant!= null && oldMerchant.getState().equals(AuthorizeMerchant.State.waiting.getCode()))){
//            AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
//            MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
//            if (response.isSuccess()) {
//                merchant.setSupplierNo(response.getSupplierNo());
//                merchant.setState(AuthorizeMerchant.State.success.getCode());
//                merchant.setFinishTime(new Date());
//            } else {
//                merchant.setReason(response.getSubMsg());
//                merchant.setState(AuthorizeMerchant.State.failed.getCode());
//                merchant.setFinishTime(new Date());
//            }
//            authorizeMerchantService.updateById(merchant);
            return ;
        }
        System.err.println(JSONObject.toJSONString(merchant));;
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
        create.setOperatorName(merchant.getOperatorName());
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

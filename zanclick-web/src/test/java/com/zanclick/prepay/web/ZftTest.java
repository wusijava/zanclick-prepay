//package com.zanclick.prepay.web;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.internal.util.AlipaySignature;
//import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
//import com.alipay.api.response.MybankCreditSupplychainTradeBillrepaybudgetQueryResponse;
//import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
//import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
//import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
//import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
//import com.zanclick.prepay.authorize.pay.AuthorizePayService;
//import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
//import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
//import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
//import com.zanclick.prepay.authorize.service.SupplyChainTradeService;
//import com.zanclick.prepay.authorize.util.SupplyChainUtils;
//import com.zanclick.prepay.authorize.vo.Refund;
//import com.zanclick.prepay.authorize.vo.RefundResult;
//import com.zanclick.prepay.authorize.vo.RegisterMerchant;
//import com.zanclick.prepay.authorize.vo.SuppilerCreate;
//import com.zanclick.prepay.common.config.JmsMessaging;
//import com.zanclick.prepay.common.config.SendMessage;
//import com.zanclick.prepay.common.utils.DataUtil;
//import com.zanclick.prepay.common.utils.StringUtils;
//import com.zanclick.prepay.order.entity.PayOrder;
//import com.zanclick.prepay.order.service.PayOrderService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author lvlu
// * @date 2019-07-06 14:32
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = WebApplication.class)
//public class ZftTest {
//
//    @Autowired
//    private AuthorizeMerchantService authorizeMerchantService;
//    @Autowired
//    private AuthorizeConfigurationService authorizeConfigurationService;
//    @Autowired
//    private AuthorizePayService authorizePayService;
//    @Autowired
//    private PayOrderService payOrderService;
//    @Autowired
//    private SupplyChainTradeService supplyChainTradeService;
//    @Autowired
//    private AuthorizeOrderService authorizeOrderService;
//
//    @Test
//    public void sssss(){
//        PayOrder order = payOrderService.queryById(14L);
//        AuthorizeOrder authorizeOrder = authorizeOrderService.queryByRequestNo(order.getRequestNo());
//        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
//        MybankCreditSupplychainTradeBillrepaybudgetQueryResponse response = SupplyChainUtils.tradeBillRepayBudgetQuery(authorizeOrder.getAuthNo(),configuration);
//
//        System.err.println(JSONObject.toJSONString(
//                response
//        ));
//    }
//
//    @Test
//    public void ssss(){
//        List<PayOrder> payOrderList = payOrderService.queryList(new PayOrder());
//        for (PayOrder order:payOrderList){
//            AuthorizeMerchant merchant = queryOne(order.getMerchantNo());
//            if (merchant == null){
//                payOrderService.deleteById(order.getId());
//            }
//            order.setWayId(merchant.getWayId());
//            order.setStoreName(merchant.getStoreName());
//            if (order.getState().equals(0) || order.getState().equals(-1)){
//                order.setDealState(PayOrder.DealState.notice_wait.getCode());
//            }
//        }
//
//    }
//
//    private AuthorizeMerchant queryOne(String merchantNo){
//        AuthorizeMerchant merchant = new AuthorizeMerchant();
//        merchant.setState(1);
//        merchant.setMerchantNo(merchantNo);
//        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(merchant);
//        if (DataUtil.isNotEmpty(merchantList)){
//            return merchantList.get(0);
//        }
//        return null;
//    }
//
//
//    @Test
//    public void sss(){
//        List<RegisterMerchant> merchantList = ExcelUtil.readExcel("E:\\excel\\2019110901.xls","Sheet1");
//        for (RegisterMerchant merchant:merchantList){
//            createAuthorizeMerchant(merchant);
//        }
//    }
//
//    @Test
//    public void ss(){
//        AuthorizeMerchant query = new AuthorizeMerchant();
//        query.setState(0);
//        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
//        for (AuthorizeMerchant merchant:merchantList){
//            try {
//                createSupplier(merchant);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    @Test
//    public void s(){
//        AuthorizeMerchant merchant = authorizeMerchantService.queryById(3332L);
//        merchant.setSellerNo("15915702607");
//        updateSupplier(merchant);
//    }
//
//
//    @Test
//    public void refund(){
//        Refund refund = new Refund();
//        refund.setOutTradeNo("157320481727640607596");
//        refund.setAmount("960");
//        refund.setOutRequestNo(StringUtils.getTradeNo());
//        refund.setReason("业务回退");
//        refund.setType(0);
//        RefundResult result = authorizePayService.refund(refund);
//        System.err.println(result);
//    }
//
//    @Test
//    public void refunds(){
//        PayOrder order = payOrderService.queryById(53l);
//        SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE,JSONObject.toJSONString(order));
//    }
//
//
//    /**
//     * 查询是否有正在审核中的商户
//     *
//     * @param dto
//     * @return
//     */
//    private AuthorizeMerchant createAuthorizeMerchant(RegisterMerchant dto) {
//        AuthorizeMerchant queryMerchant = new AuthorizeMerchant();
//        queryMerchant.setMerchantNo(dto.getMerchantNo());
//        List<AuthorizeMerchant>  merchantList = authorizeMerchantService.queryList(queryMerchant);
//        if (DataUtil.isNotEmpty(merchantList)){
//            boolean flag = false;
//            for (AuthorizeMerchant merchant:merchantList){
//                if (merchant.isSuccess()){
//                    flag = true;
//                }
//            }
//            if (flag){
//                return null;
//            }
//        }
//
//        AuthorizeMerchant merchant = new AuthorizeMerchant();
//        merchant.setAppId(dto.getAppId());
//        merchant.setWayId(dto.getWayId());
//        merchant.setMerchantNo(dto.getMerchantNo());
//        merchant.setContactName(dto.getContactName());
//        merchant.setContactPhone(dto.getContactPhone());
//        merchant.setCreateTime(new Date());
//        merchant.setName(dto.getName());
//        merchant.setOperatorName(dto.getOperatorName());
//        merchant.setStoreSubjectName(dto.getStoreSubjectName());
//        merchant.setStoreSubjectCertNo(dto.getStoreSubjectCertNo());
//        merchant.setStoreNo(dto.getStoreNo());
//        merchant.setStoreName(dto.getStoreName());
//        merchant.setStoreProvince(dto.getStoreProvince());
//        merchant.setStoreCity(dto.getStoreCity());
//        merchant.setStoreCounty(dto.getStoreCounty());
//        merchant.setStoreProvinceCode(dto.getStoreProvinceCode());
//        merchant.setStoreCityCode(dto.getStoreCityCode());
//        merchant.setStoreCountyCode(dto.getStoreCountyCode());
//        merchant.setSellerNo(dto.getSellerNo());
//        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
//        authorizeMerchantService.insert(merchant);
//        System.err.println(merchant.getId());
//        return merchant;
//    }
//
//
//    /**
//     * 签约商户
//     *
//     * @param merchant
//     * @return （原因）没有返回，则为签约成功
//     */
//    private void createSupplier(AuthorizeMerchant merchant) {
//        AuthorizeMerchant oldMerchant = authorizeMerchantService.queryByAliPayLoginNo(merchant.getSellerNo());
//        if (oldMerchant != null ) {
//            if (oldMerchant.getState().equals(AuthorizeMerchant.State.success.getCode())){
//                merchant.setSupplierNo(oldMerchant.getSupplierNo());
//                merchant.setState(AuthorizeMerchant.State.success.getCode());
//                merchant.setFinishTime(new Date());
//                authorizeMerchantService.updateById(merchant);
//                return ;
//            }else if (oldMerchant.getState().equals(AuthorizeMerchant.State.failed.getCode())){
//                merchant.setReason(oldMerchant.getReason());
//                merchant.setState(AuthorizeMerchant.State.failed.getCode());
//                merchant.setFinishTime(new Date());
//                authorizeMerchantService.updateById(merchant);
//                return ;
//            }
//        }
//        if ( oldMerchant == null || (oldMerchant!= null && oldMerchant.getState().equals(AuthorizeMerchant.State.waiting.getCode()))){
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
//            return ;
//        }
//    }
//
//
//    private void updateSupplier(AuthorizeMerchant merchant) {
//        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
//        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
//        if (response.isSuccess()) {
//            merchant.setSupplierNo(response.getSupplierNo());
//            merchant.setState(AuthorizeMerchant.State.success.getCode());
//            merchant.setFinishTime(new Date());
//            authorizeMerchantService.updateById(merchant);
//        } else {
//            merchant.setReason(response.getSubMsg());
//            merchant.setState(AuthorizeMerchant.State.failed.getCode());
//            merchant.setFinishTime(new Date());
//            System.err.println(response.getSubMsg());
//        }
//    }
//
//
//    /**
//     * 创建商户
//     *
//     * @param merchant
//     * @return
//     */
//    private SuppilerCreate create(AuthorizeMerchant merchant) {
//        SuppilerCreate create = new SuppilerCreate();
//        create.setStoreNo(merchant.getSellerNo());
//        create.setSellerName(merchant.getName());
//        create.setRcvContactEmail(null);
//        create.setRcvLoginId(merchant.getSellerNo());
//        create.setRcvContactName(merchant.getContactName());
//        create.setRcvContactPhone(merchant.getContactPhone());
//        create.setOperatorName(merchant.getOperatorName());
//        create.setStoreNo(merchant.getStoreNo());
//        create.setStoreName(merchant.getStoreName());
//        create.setStoreSubjectName(merchant.getStoreSubjectName());
//        create.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
//        create.setStoreProvince(merchant.getStoreProvince());
//        create.setStoreCity(merchant.getStoreCity());
//        create.setStoreCounty(merchant.getStoreCounty());
//        return create;
//    }
//}

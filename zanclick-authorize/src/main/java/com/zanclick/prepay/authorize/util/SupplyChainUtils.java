package com.zanclick.prepay.authorize.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.exception.SupplyChainException;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import com.zanclick.prepay.authorize.vo.SupplyChainCreate;
import com.zanclick.prepay.authorize.vo.SupplyChainPay;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lvlu
 * @date 2019-05-05 13:45
 **/
@Slf4j
public class SupplyChainUtils {

    private static final String SALE_PD_CODE = "01025200002000003643";
    private static final Integer BILL_DATE = 26;

    public static void main(String[] args) {
    }




    /**
     * 网商垫资交易创建
     *
     * @param create            预授权冻结时支付宝返回的28位预授权编号
     * @param configuration   预授权顾客支付宝账户id
     * @return ev_seq_no 业务事件受理的流水号，作为异步回调的业务处理参数依据
     */
    public static String tradeCreate(SupplyChainCreate create, AuthorizeConfiguration configuration) throws SupplyChainException {

        Member seller = createSeller(null, create.getRcvLoginId());
        Member buyer = createBuyer(configuration);
        MybankCreditSupplychainTradeCreateModel model = new MybankCreditSupplychainTradeCreateModel();
        model.setTradeType("FACTORING");
        model.setRequestId(createRequestId(configuration.getRoleId()));
        model.setSeller(seller);
        model.setBuyer(buyer);
        model.setPayAccount(createPayAccount(configuration));
        model.setRcvAccount(createRcvAccount(create.getRcvLoginId(), create.getRcvAliPayName()));
        model.setOutOrderTitle(create.getTitle());
        model.setSalePdCode(SALE_PD_CODE);
        model.setOutOrderNo(createOutOrderNo(configuration.getRoleId(),create.getAuthNo()));
        model.setChannel("FQZBL");
        model.setExpireDate(getExpireDate(create.getFqNum()));
        model.setTradeAmount(create.getAmount());
        TradeCreateExtData extData = new TradeCreateExtData();
        //垫资订单添加采购内容和账款备注
        extData.setPurchase_content(create.getPurchaseContent());
        extData.setReceivable_remark(create.getReceivableRemark());
        extData.setRcv_login_id(create.getRcvLoginId());
        extData.setRcv_name(create.getRcvAliPayName());
        extData.setRcv_contact_name(create.getRcvContactName());
        extData.setRcv_contact_phone(create.getRcvContactPhone());
        extData.setRcv_contact_email(create.getRcvContactEmail());
        extData.setAuth_no(create.getAuthNo());
        extData.setOut_merch_order_no(create.getOutMerOrderNo());
        extData.setOperation_id(create.getOperationId());
        extData.setOut_request_no(create.getOutRequestNo());
        BigDecimal freezeAmount = new BigDecimal(create.getAmount()).multiply(new BigDecimal("1.1")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        extData.setFreeze_amount(freezeAmount.toString());
        extData.setFreeze_date(DateUtil.formatDate(create.getFreezeDate(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        extData.setExpire_date(DateUtil.formatDate(create.getExpireDate(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        extData.setFreeze_terms(create.getFqNum());
        extData.setFreeze_alipay_id(create.getFreezeAliPayId());
        model.setExtData(JSON.toJSONString(extData));
        MybankCreditSupplychainTradeCreateRequest request = new MybankCreditSupplychainTradeCreateRequest();
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            log.error("开始垫资:{}",JSONObject.toJSONString(request));
            MybankCreditSupplychainTradeCreateResponse response = alipayClient.execute(request);
            log.error("结束垫资:{}",JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return model.getRequestId();
            } else {
                throw new SupplyChainException(response.getSubCode(),response.getSubMsg(),model.getRequestId());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 网点垫资还款支付，需要网商已完成垫资放款，支付宝预授权解冻转支付
     *
     * @param pay   还款支付详情
     * @param configuration 配置信息
     * @return requestId 业务事件受理的流水号，作为异步回调的业务处理参数依据
     */
    public static String tradePay(SupplyChainPay pay, AuthorizeConfiguration configuration) throws SupplyChainException{
        Member buyer = createBuyer(configuration);
        MybankCreditSupplychainTradePayModel model = new MybankCreditSupplychainTradePayModel();
        model.setTradeType("FACTORING");
        model.setRequestId(createRequestId(configuration.getRoleId()));
        model.setBuyer(buyer);
        model.setPayAmount(pay.getPayAmount());
        model.setSalePdCode(SALE_PD_CODE);
        model.setOutOrderNo(createOutOrderNo(configuration.getRoleId(),pay.getAuthNo()));
        model.setChannel("FQZBL");
        model.setExtData("{\"payableRepayType\":\"preRepay\"}");
        MybankCreditSupplychainTradePayRequest request = new MybankCreditSupplychainTradePayRequest();
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            MybankCreditSupplychainTradePayResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return model.getRequestId();
            } else {
                throw new SupplyChainException(response.getSubCode(),response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 网点垫资交易取消，需要在交易创建，放款前调用
     *
     * @param auth_no 预授权冻结时支付宝返回的28位预授权编号
     * @param configuration
     * @return requestId 业务事件受理的流水号，作为异步回调的业务处理参数依据
     */
    public static String tradeCancel(String auth_no,AuthorizeConfiguration configuration) throws SupplyChainException{
        Member buyer = createBuyer(configuration);
        MybankCreditSupplychainTradeCancelModel model = new MybankCreditSupplychainTradeCancelModel();
        model.setTradeType("FACTORING");
        model.setRequestId(createRequestId(configuration.getRoleId()));
        model.setBuyer(buyer);
        model.setSalePdCode(SALE_PD_CODE);
        model.setOutOrderNo(createOutOrderNo(configuration.getRoleId(),auth_no));
        model.setChannel("FQZBL");
        MybankCreditSupplychainTradeCancelRequest request = new MybankCreditSupplychainTradeCancelRequest();
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            MybankCreditSupplychainTradeCancelResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return model.getRequestId();
            } else {
                throw new SupplyChainException(response.getSubCode(),response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建供应商
     */
    public static MybankCreditSupplychainFactoringSupplierCreateResponse createSupplier(
            SuppilerCreate create,AuthorizeConfiguration configuration) throws SupplyChainException{
        MybankCreditSupplychainFactoringSupplierCreateResponse response = null;
        MybankCreditSupplychainFactoringSupplierCreateRequest request = new MybankCreditSupplychainFactoringSupplierCreateRequest();
        MybankCreditSupplychainFactoringSupplierCreateModel model = new MybankCreditSupplychainFactoringSupplierCreateModel();
        Member buyer = createBuyer(configuration);
        model.setBuyerIpId(buyer.getIpId());
        model.setBuyerIpRoleId(buyer.getIpRoleId());
        model.setBuyerSite(buyer.getSite());
        model.setBuyerSiteUserId(buyer.getSiteUserId());
        model.setSellerLoginId(create.getRcvLoginId());
        model.setSellerContactName(create.getRcvContactName());
        model.setSellerContactEmail(create.getRcvContactEmail());
        model.setSellerContactPhone(create.getRcvContactPhone());
        model.setRcvAccountType("ALIPAY");
        model.setSellerBankAccName(create.getSellerName());
        model.setOperatorName(create.getOperatorName());
        model.setStoreNo(create.getStoreNo());
        model.setStoreName(create.getStoreName());
        model.setStoreSubjectName(create.getStoreSubjectName());
        model.setStoreSubjectCertNo(create.getStoreSubjectCertNo());
        model.setStoreProvince(create.getStoreProvince());
        model.setStoreCity(create.getStoreCity());
        model.setStoreCounty(create.getStoreCounty());
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            log.error("商户创建:{}",JSONObject.toJSONString(request));
            response = alipayClient.execute(request);
            log.error("商户创建结果:{}",JSONObject.toJSONString(response));
            return response;
        }catch (Exception e){
            log.error("商户创出错:{},{}",create.getRcvLoginId(),e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询还款方案
     * @param auth_no
     * @param configuration
     * @return
     */
    public static MybankCreditSupplychainTradeBillrepaybudgetQueryResponse tradeBillRepayBudgetQuery(String auth_no,AuthorizeConfiguration configuration) throws SupplyChainException{
        Member buyer = createBuyer(configuration);
        MybankCreditSupplychainTradeBillrepaybudgetQueryRequest request = new MybankCreditSupplychainTradeBillrepaybudgetQueryRequest();
        MybankCreditSupplychainTradeBillrepaybudgetQueryModel model = new MybankCreditSupplychainTradeBillrepaybudgetQueryModel();
        model.setBuyer(buyer);
        model.setChannel("FQZBL");
        model.setSalePdCode(SALE_PD_CODE);
        model.setOutOrderNo(createOutOrderNo(configuration.getRoleId(),auth_no));
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            MybankCreditSupplychainTradeBillrepaybudgetQueryResponse response = alipayClient.execute(request);
            return response;
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String queryLimit(AuthorizeConfiguration configuration){
        MybankCreditCreditriskGuarschemeQueryRequest request = new MybankCreditCreditriskGuarschemeQueryRequest();
        MybankCreditCreditriskGuarschemeQueryModel model = new MybankCreditCreditriskGuarschemeQueryModel();
        model.setBsnType("TYZBL");
        model.setUser(createBuyer(configuration));
        model.setSalePdCode(SALE_PD_CODE);
        request.setBizModel(model);
        AlipayClient alipayClient = getAlipayClient(configuration);
        try {
            MybankCreditCreditriskGuarschemeQueryResponse response = alipayClient.execute(request);
            return response.getAvailableAmt();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

    }


    @Data
    public static class TradeCreateExtData {
        private String rcv_login_id;
        private String rcv_name;
        private String rcv_contact_name;
        private String rcv_contact_phone;
        private String rcv_contact_email;
        private String purchase_content = "话费分期";
        private String receivable_remark = "";
        private String rcv_fee_rate_term_unit = "DAY";
        private String rcv_fee_rate = "0";
        private String auth_no;
        private String out_merch_order_no;
        private String operation_id;
        private String out_request_no;
        private String freeze_amount;
        private String freeze_date;
        private String expire_date;
        private Integer freeze_terms;
        private String terms_unit = "M";
        private String freeze_alipay_id;
    }

    private static Account createPayAccount(AuthorizeConfiguration configuration) {
        return createAccount(configuration.getMyBankUid(), configuration.getName());
    }

    private static Account createRcvAccount(String uid, String alipayName) {
        return createAccount(uid, alipayName);
    }

    private static Account createAccount(String uid, String alipayName) {
        Account account = new Account();
        account.setAccountType("ALIPAY");
        account.setAccountNo(uid);
        account.setAccountName(alipayName);
        return account;
    }

    private static Member createBuyer(AuthorizeConfiguration configuration) {
        Member buyer = new Member();
        buyer.setIpId(configuration.getIpId());
        buyer.setIpRoleId(configuration.getRoleId());
        buyer.setSiteLoginId(configuration.getMyBankSellerNo());
        buyer.setSiteUserId(configuration.getMyBankUid());
        buyer.setSite("ALIPAY");
        buyer.setUseType("SITE");
        return buyer;
    }

    private static String createOutOrderNo(String role_id, String auth_no) {
        return role_id + "_" + auth_no;
    }

    public static String getAuthNoFromOutOrderNo(String out_order_no){
        if(DataUtil.isEmpty(out_order_no)){
            return null;
        }
        String[] arr = out_order_no.split("_");
        if(arr.length==2){
            return arr[1];
        }
        return null;
    }

    public static Date getExpireDate(Integer fq_num) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, fq_num);
        c.set(Calendar.DAY_OF_MONTH, BILL_DATE);
        return c.getTime();
    }


    private static AlipayClient getAlipayClient(AuthorizeConfiguration configuration) {
        return new DefaultAlipayClient(configuration.getGateway(), configuration.getMyBankAppId(), configuration.getMyBankPrivateKey()
                , configuration.getFormat(), configuration.getCharset(), configuration.getMyBankPublicKey(), configuration.getSignType());
    }

    /**
     * 创建收款门店数据体
     *
     * @param userId  收款门店支付宝账号PID
     * @param loginId 收款门店支付宝账号
     * @return
     */
    private static Member createSeller(String userId, String loginId) {
        Member seller = new Member();
        seller.setSite("ALIPAY");
        seller.setUseType("SITE");
        seller.setSiteUserId(userId);
        seller.setSiteLoginId(loginId);
        return seller;
    }

    private static String createRequestId(String role_id) {
        String date = DateUtil.formatDate(new Date(), DateUtil.PATTERN_YYYYMMDDHHMMSS);
        String uniqId = StringUtils.createRandom(true, 8);
        return role_id + "_" + date + "_" + uniqId;
    }


}

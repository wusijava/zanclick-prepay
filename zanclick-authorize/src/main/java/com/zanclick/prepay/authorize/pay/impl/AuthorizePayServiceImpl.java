package com.zanclick.prepay.authorize.pay.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.*;
import com.zanclick.prepay.authorize.dto.*;
import com.zanclick.prepay.authorize.entity.*;
import com.zanclick.prepay.authorize.enums.AliConstants;
import com.zanclick.prepay.authorize.enums.Constants;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.*;
import com.zanclick.prepay.authorize.util.AuthorizePayUtil;
import com.zanclick.prepay.authorize.util.AuthorizeUtil;
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;
import com.zanclick.prepay.supplychain.service.MyBankSupplyChainService;
import com.zanclick.prepay.supplychain.service.SupplyChainTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预授权支付
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public class AuthorizePayServiceImpl implements AuthorizePayService {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizeFeeService authorizeFeeService;
    @Autowired
    private AuthorizeOrderService authorizeOrderService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;
    @Autowired
    private AuthorizeRefundOrderService authorizeRefundOrderService;
    @Autowired
    private AuthorizeOrderRefundRecordService authorizeOrderRefundRecordService;
    @Autowired
    private AuthorizeOrderRecordService authorizeOrderRecordService;
    @Autowired
    private MyBankSupplyChainService myBankSupplyChainService;
    @Autowired
    private SupplyChainTradeService supplyChainTradeService;

    private static String NOTIFY_URL;

    @Value("${authorize.notify}")
    public void setAccessKey(String notifyUrl) {
        NOTIFY_URL = notifyUrl;
    }

    @Value("${timeout.authorize.prepay}")
    private String prePay;

    @Override
    public PayResult prePay(PayDTO dto) {
        PayResult result = new PayResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        AuthorizeMerchant merchant = authorizeMerchantService.queryMerchant(dto.getMerchantNo());
        if (merchant == null || !merchant.isSuccess()) {
            result.setFail();
            result.setMessage("未签约商户无法进行预授权操作");
            return result;
        }
        AuthorizeOrder order = createAuthorizeOrder(dto, merchant);
        result.setOutTradeNo(dto.getOutOrderNo());
        result.setTradeNo(order.getRequestNo());
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(order.getConfigurationId());
        AlipayClient client = authorizeConfigurationService.getAlipayClient(configuration);
        AuthorizeDTO payModal = createPayModal(order);
        payModal.setPayee_user_id(configuration.getIsv_uid());
        order.setRequestContent(payModal.toString());
        AlipayFundAuthOrderVoucherCreateResponse createResponse = AuthorizePayUtil.qrFreeze(client, NOTIFY_URL, payModal);
        if (createResponse.isSuccess()) {
            order.setQrCodeUrl(createResponse.getCodeValue());

            result.setQrCodeUrl(order.getQrCodeUrl());
            result.setEachMoney(order.getFee().getEachMoney());
            result.setFirstMoney(order.getFee().getFirstMoney());
            result.setSuccess();
        } else {
            order.setState(AuthorizeOrder.State.failed.getCode());
            authorizeOrderService.handleAuthorizeOrder(order);

            result.setFail();
            result.setMessage(createResponse.getSubMsg());
        }
        authorizeOrderService.updateById(order);
        return result;
    }


    @Override
    public SettleResult settle(SettleDTO dto) {
        SettleResult result = new SettleResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null || !order.isPayed()) {
            String message = order == null ? "交易订单号有误" : "订单状态异常，无法结算";
            result.setMessage(message);
            result.setFail();
            return result;
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByAuthNo(order.getAuthNo());
        if (trade != null && !trade.isFail()) {
            result.setMessage("已结算或正在结算中");
            result.setFail();
            return result;
        }
        trade = createSupplyChainTrade(order);
        if (trade.isFail()) {
            result.setMessage(trade.getFailReason());
            result.setFail();
            return result;
        }
        result.setSuccess();
        return result;
    }

    @Override
    public FreezeResult unFreeze(UnFreezeDTO dto) {
        FreezeResult result = new FreezeResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        String desc = dto.isUnFree() ? "解冻" : "转支付";
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null || order.isUnPay()) {
            String message = order == null ? "交易订单号有误" : "订单状态异常，无法进行" + desc;
            result.setMessage(message);
            result.setFail();
            return result;
        }
        List<AuthorizeOrderRecord> recordList = authorizeOrderRecordService.queryByAuthNoAndNum(order.getAuthNo(), dto.getNum());
        if (dto.getNum() != 0) {
            AuthorizeOrderRecord record = recordList.get(0);
            if (DataUtil.isEmpty(recordList) || !record.isWait()) {
                String message = record == null ? "期数有误" : dto.getNum() + "期已经进行" + desc + ",无法再次操作";
                result.setMessage(message);
                result.setFail();
                return result;
            }
        }
        if (DataUtil.isEmpty(recordList)) {
            recordList = authorizeOrderRecordService.createAuthorizeOrderRecord(order);
        }
        String money = getRefundMoney(recordList);
        if (!MoneyUtil.zeroMoney(money)) {
            result.setMessage("可退款金额为0.00");
            result.setFail();
            return result;
        }
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(order.getConfigurationId());
        AlipayClient client = authorizeConfigurationService.getAlipayClient(configuration);
        Integer state = null;
        if (dto.isUnFree()) {
            state = AuthorizeOrderRecord.State.unfreed.getCode();
            AlipayFundAuthOrderUnfreezeResponse unfreezeResponse = createUnFreeze(order, dto.getOrderNo(), dto.getReason(), money, client);
            if (!(unfreezeResponse.isSuccess() && unfreezeResponse.getStatus().equals(Constants.TRADE_SUCCESS))) {
                result.setMessage(unfreezeResponse.getSubMsg());
                result.setFail();
                return result;
            }
        } else {
            state = AuthorizeOrderRecord.State.payed.getCode();
            AlipayTradePayResponse payResponse = createPay(order, dto, money, client, configuration);
            if (!payResponse.isSuccess()) {
                result.setMessage(payResponse.getSubMsg());
                result.setFail();
                return result;
            }
        }
        authorizeOrderRecordService.handleRecordList(recordList, state, dto.getOrderNo());
        boolean lastRecord = authorizeOrderRecordService.queryListRecord(order.getAuthNo(), dto.getNum());
        if (lastRecord) {
            order.setState(AuthorizeOrder.State.settled.getCode());
            authorizeOrderService.handleAuthorizeOrder(order);
        }
        result.setSuccess();
        return result;
    }

    @Override
    public QueryResult query(QueryDTO dto) {
        QueryResult result = new QueryResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null) {
            result.setMessage("交易订单号有误");
            result.setFail();
            return result;
        }
        if (order.isUnPay()) {
            boolean cancel = authorizeOrderService.maintainAuthorizeOrder(order);
            if (cancel) {
                result.setMessage("交易超时，已撤销");
                result.setFail();
                result.setState(order.getState());
                return result;
            }
            AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
            AuthorizeDTO query = new AuthorizeDTO();
            query.setOut_request_no(order.getRequestNo());
            query.setOut_order_no(order.getOrderNo());
            AlipayFundAuthOperationDetailQueryResponse queryResponse = AuthorizePayUtil.query(client, query);
            if (queryResponse.getStatus() != null) {
                if (queryResponse.getStatus().equals(Constants.TRADE_SUCCESS)) {
                    order.setOperationId(queryResponse.getOperationId());
                    order.setAuthNo(queryResponse.getAuthNo());
                    order.setBuyerId(queryResponse.getPayerUserId());
                    order.setBuyerNo(queryResponse.getPayerLogonId());
                    order.setState(AuthorizeOrder.State.payed.getCode());
                } else if (queryResponse.getStatus().equals(Constants.INIT)) {
                    order.setState(AuthorizeOrder.State.paying.getCode());
                } else if (queryResponse.getStatus().equals(AliConstants.TRADE_CLOSED)) {
                    order.setState(AuthorizeOrder.State.closed.getCode());
                }
                authorizeOrderService.handleAuthorizeOrder(order);
            }
        }
        result.setState(order.getState());
        result.setSuccess();
        return result;
    }

    @Override
    public QueryResult cancel(QueryDTO dto) {
        QueryResult result = new QueryResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null) {
            result.setMessage("交易订单号有误");
            result.setFail();
            return result;
        }
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
        AuthorizeDTO authorizeDTO = new AuthorizeDTO();
        authorizeDTO.setOut_order_no(order.getOrderNo());
        authorizeDTO.setOut_request_no(order.getRequestNo());
        boolean cancel = AuthorizePayUtil.cancel(client, authorizeDTO);
        if (cancel) {
            result.setMessage("交易超时，已撤销");
            result.setFail();
            return result;
        }
        order.setState(AuthorizeOrder.State.closed.getCode());
        authorizeOrderService.handleAuthorizeOrder(order);
        result.setState(order.getState());
        result.setSuccess();
        return result;
    }

    @Override
    public RefundResult refund(RefundDTO dto) {
        RefundResult result = new RefundResult();
        String check = dto.check();
        if (check != null) {
            result.setMessage(check);
            result.setFail();
            return result;
        }
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        result.setRefundNo(dto.getRefundNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null || order.isFail() || order.isUnPay()) {
            String message = order == null ? "交易订单号有误" : order.isUnPay() ? "未支付订单无法退款" : "交易已关闭,无法退款";
            result.setMessage(message);
            result.setFail();
            return result;
        }
        AuthorizeRefundOrder refund = authorizeRefundOrderService.queryByRefundNo(dto.getRefundNo());
        if (refund != null && refund.isSuccess()) {
            result.setAmount(order.getFee().getMoney());
            result.setRefundAmount(refund.getAmount());
            result.setRefundTime(refund.getCreateTime());
            result.setState(order.getState());
            result.setIsChange("N");
            result.setSuccess();
            return result;
        }
        if (refund != null && refund.isFail()) {
            result.setMessage(refund.getReason());
            result.setFail();
            return result;
        }
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
//        List<AuthorizeOrderRecord> recordList = authorizeOrderRecordService.queryWaitByAuthNo(order.getAuthNo());
//        if (DataUtil.isEmpty(recordList)) {
//            recordList = authorizeOrderRecordService.createAuthorizeOrderRecord(order);
//        }
//        String refundMoney = getRefundMoney(recordList);
        String refundMoney = order.getFee().getOrderRealMoney();
        if (!MoneyUtil.zeroMoney(refundMoney)) {
            result.setMessage("可退款金额为0");
            result.setFail();
            return result;
        }
        String remark = "交易退款-" + refundMoney + "元";
        if (DataUtil.isNotEmpty(dto.getReason())) {
            remark = dto.getReason();
        }
        refund = authorizeRefundOrderService.createRefundOrder(refundMoney, order.getOrderNo(), order.getRequestNo(), dto.getRefundNo(), dto.getReason());
        AlipayFundAuthOrderUnfreezeResponse unfreezeResponse = createUnFreeze(order, dto.getRefundNo(), remark, refundMoney, client);
        if (!(unfreezeResponse.isSuccess() && unfreezeResponse.getStatus().equals(Constants.TRADE_SUCCESS))) {
            refund.setReason(unfreezeResponse.getSubMsg());
            authorizeRefundOrderService.refundFail(refund);

            result.setMessage(unfreezeResponse.getSubMsg());
            result.setFail();
            return result;
        }
        authorizeRefundOrderService.refundSuccess(refund);
//        authorizeOrderRecordService.handleRecordList(recordList, AuthorizeOrderRecord.State.unfreed.getCode(), dto.getRefundNo());
        order.setState(AuthorizeOrder.State.refund.getCode());
        authorizeOrderService.handleAuthorizeOrder(order);
        result.setAmount(order.getFee().getMoney());
        result.setRefundAmount(refund.getAmount());
        result.setRefundTime(refund.getCreateTime());
        result.setState(order.getState());
        result.setIsChange("Y");
        result.setSuccess();
        return result;
    }

    @Override
    public PayRefundResult payRefund(PayRefundDTO dto) {
        PayRefundResult result = new PayRefundResult();
        String check = dto.check();
        if (check != null) {
            result.setMessage(check);
            result.setFail();
            return result;
        }
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null || order.isFail() || order.isUnPay()) {
            result.setMessage("交易订单号有误");
            result.setFail();
            return result;
        }
        List<AuthorizeOrderRecord> recordList = authorizeOrderRecordService.queryByTradeNo(dto.getPayTradeNo());
        if (DataUtil.isEmpty(recordList)) {
            result.setMessage("交易订单号有误");
            result.setFail();
            return result;
        }
        String refundMoney = getPayRefundMoney(recordList);
        if (!MoneyUtil.zeroMoney(refundMoney)) {
            result.setMessage("可退金额为0.00");
            result.setFail();
            return result;
        }
        AuthorizeOrderRefundRecord record = authorizeOrderRefundRecordService.queryByRefundNo(dto.getRefundNo());
        if (record == null) {
            record = authorizeOrderRefundRecordService.createRefundOrder(refundMoney, dto.getPayTradeNo(), dto.getRefundNo(), dto.getReason());
        }
        if (record.isFail()) {
            result.setMessage(record.getReason());
            result.setFail();
            return result;
        }
        if (record.isSuccess()) {
            result.setAmount(record.getAmount());
            result.setIsChange("N");
            result.setOutTradeNo(order.getOutTradeNo());
            result.setRefundAmount(record.getAmount());
            result.setRefundNo(record.getRefundNo());
            result.setRefundTime(record.getCreateTime());
            result.setTradeNo(order.getRequestNo());
            return result;
        }
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
        String refundNo = dto.getRefundNo() == null ? dto.getRefundNo() : StringUtils.getTradeNo();
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setOut_request_no(refundNo);
        modal.setOut_trade_no(dto.getPayTradeNo());
        modal.setRefund_amount(refundMoney);
        AlipayTradeRefundResponse response = AuthorizePayUtil.payRefund(client, modal);
        if (!response.isSuccess()) {
            record.setReason(response.getSubMsg());
            authorizeOrderRefundRecordService.refundFail(record);
            result.setFail();
            result.setMessage(response.getSubMsg());
            return result;
        }
        authorizeOrderRefundRecordService.refundSuccess(record);
        authorizeOrderRecordService.handleRecordList(recordList, AuthorizeOrderRecord.State.refund.getCode(), dto.getRefundNo());
        result.setAmount(record.getAmount());
        result.setIsChange("Y");
        result.setOutTradeNo(order.getOutTradeNo());
        result.setRefundAmount(record.getAmount());
        result.setRefundNo(record.getRefundNo());
        result.setRefundTime(record.getCreateTime());
        result.setTradeNo(order.getRequestNo());
        result.setSuccess();
        return result;
    }

    @Override
    public RefundResult refundQuery(RefundDTO dto) {
        RefundResult result = new RefundResult();
        String check = dto.check();
        if (check != null) {
            result.setMessage(check);
            result.setFail();
            return result;
        }
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setTradeNo(dto.getTradeNo());
        result.setRefundNo(dto.getRefundNo());
        AuthorizeOrder order = queryByTradeNoOrOutTradeNo(dto.getTradeNo(), dto.getOutTradeNo());
        if (order == null) {
            result.setMessage("交易订单号有误");
            result.setFail();
            return result;
        }
        AuthorizeRefundOrder refund = authorizeRefundOrderService.queryByRefundNo(dto.getRefundNo());
        if (refund == null) {
            result.setMessage("退款订单号有误");
            result.setFail();
            return result;
        }
        result.setAmount(order.getFee().getOrderRealMoney());
        result.setRefundAmount(refund.getAmount());
        result.setRefundTime(refund.getCreateTime());
        result.setIsChange("N");
        result.setState(order.getState());
        result.setSuccess();
        return result;
    }

    /**
     * 创建解冻订单
     *
     * @param order   预授权订单
     * @param orderNo 订单号
     * @param reason  金额
     * @param money   金额
     * @param client  支付配置
     * @return
     */
    private AlipayFundAuthOrderUnfreezeResponse createUnFreeze(AuthorizeOrder order, String orderNo, String reason, String money, AlipayClient client) {
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setAuth_no(order.getAuthNo());
        modal.setOut_request_no(orderNo);
        modal.setAmount(money);
        modal.setRemark(reason);
        return AuthorizePayUtil.unFreeze(client, modal);
    }

    /**
     * 创建转支付订单
     *
     * @param order  预授权订单
     * @param dto    请求参数
     * @param money  金额
     * @param client 支付配置
     * @return
     */
    private AlipayTradePayResponse createPay(AuthorizeOrder order, UnFreezeDTO dto, String money, AlipayClient client, AuthorizeConfiguration configuration) {
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setAuth_no(order.getAuthNo());
        modal.setSeller_id(configuration.getIsv_uid());
        modal.setOut_trade_no(dto.getOrderNo());
        modal.setAuth_confirm_mode("NOT_COMPLETE");
        modal.setProduct_code("PRE_AUTH");
        modal.setBuyer_id(order.getBuyerId());
        modal.setTotal_amount(money);
        modal.setSubject(dto.getReason());
        modal.setScene("bar_code");
        ExtendParam param = new ExtendParam();
        param.setSys_service_provider_id("2088721239794361");
        modal.setExtra_param(param);
        return AuthorizePayUtil.pay(client, NOTIFY_URL, modal);
    }

    /**
     * 创建预授权订单
     *
     * @param order 参数
     * @return
     */
    private AuthorizeDTO createPayModal(AuthorizeOrder order) {
        AuthorizeDTO dto = new AuthorizeDTO();
        dto.setOut_request_no(order.getRequestNo());
        dto.setOut_order_no(order.getOrderNo());
        dto.setOrder_title(order.getTitle());
        dto.setAmount(order.getFee().getOrderRealMoney());
        dto.setPay_timeout(order.getTimeout() + "m");
        dto.setExtra_param(getExtendParam());
        dto.setProduct_code("PRE_AUTH");
        dto.setEnable_pay_channels(PayWay.typeList.get(order.getPayWay()));
        return dto;
    }


    /**
     * 创建预授权订单
     *
     * @param dto      参数
     * @param merchant 参数
     * @return
     */
    private AuthorizeOrder createAuthorizeOrder(PayDTO dto, AuthorizeMerchant merchant) {
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        AuthorizeOrder order = new AuthorizeOrder();
        order.setMoney(dto.getAmount());
        order.setOutTradeNo(dto.getOutOrderNo());
        order.setMerchantNo(dto.getMerchantNo());
        order.setTimeout(getTimeOut());
        order.setTitle(dto.getDesc());
        order.setStoreNo(merchant.getStoreNo());
        order.setDealType(AuthorizeOrder.DealType.SCAN.getCode());
        order.setPayWay(getPayWay(dto.getPayWay()));
        order.setSettleDate(getSettleDate());
        order.setSellerNo(merchant.getSellerNo());
        order.setSellerName(merchant.getName());
        order.setContactName(merchant.getContactName());
        order.setContactPhone(merchant.getContactPhone());
        order.setCreateTime(new Date());
        order.setOrderNo(StringUtils.getTradeNo());
        order.setRequestNo(StringUtils.getTradeNo());
        order.setState(AuthorizeOrder.State.unPay.getCode());
        order.setSettleType(AuthorizeOrder.SettleType.NO.getCode() );
        order.setConfigurationId(configuration.getId());
        order.setAppId(merchant.getAppId());
        createOrderFee(order);
        authorizeOrderService.insert(order);
        return order;
    }


    /**
     * 根据订单号查询或者屋外部订单号查询
     *
     * @param tradeNo
     * @param outTradeNo
     */
    private AuthorizeOrder queryByTradeNoOrOutTradeNo(String tradeNo, String outTradeNo) {
        AuthorizeOrder order = null;
        if (tradeNo != null) {
            order = authorizeOrderService.queryByRequestNo(tradeNo);
        }
        if (order == null && outTradeNo != null) {
            order = authorizeOrderService.queryByOutTradeNo(outTradeNo);
        }
        return order;
    }


    /**
     * 创建网商垫资订单
     *
     * @param order
     * @return
     */
    private SupplyChainTrade createSupplyChainTrade(AuthorizeOrder order) {
        SupplyChainTrade trade = new SupplyChainTrade();
        trade.setAmount(order.getMoney());
        trade.setAuthNo(order.getAuthNo());
        trade.setCreateTime(new Date());
        trade.setFqNum(order.getFee().getCycle());
        trade.setTitle(order.getTitle());
        trade.setTotalAmount(order.getMoney());
        trade.setRcvLoginId(order.getSellerNo());
        trade.setRcvContactPhone(order.getContactPhone());
        trade.setRcvContactName(order.getContactName());
        trade.setRcvAlipayName(order.getSellerName());
        trade.setOutTradeNo(StringUtils.getTradeNo());
        trade.setOutRequestNo(order.getRequestNo());
        trade.setOpId(order.getOperationId());
        trade.setFreezeUserId(order.getBuyerId());

        trade.setFreezeDate(order.getFinishTime());
        trade.setExpireDate(DateUtil.addTime(order.getFinishTime(),3,Calendar.YEAR));
        trade.setMybankFee(order.getFee().getServiceMoney());
        myBankSupplyChainService.tradeCreate(trade);
        return trade;
    }


    /**
     * 获取退款金额
     *
     * @param recordList 参数
     * @return
     */
    public String getRefundMoney(List<AuthorizeOrderRecord> recordList) {
        BigDecimal refundMoney = new BigDecimal("0.00");
        if (DataUtil.isEmpty(recordList)) {
            return refundMoney.toString();
        }
        for (AuthorizeOrderRecord record : recordList) {
            if (record.isWait()) {
                refundMoney = refundMoney.add(new BigDecimal(record.getMoney()));
            }
        }
        return refundMoney.toString();
    }

    /**
     * 获取退款金额
     *
     * @param recordList 参数
     * @return
     */
    public String getPayRefundMoney(List<AuthorizeOrderRecord> recordList) {
        BigDecimal refundMoney = new BigDecimal("0.00");
        if (DataUtil.isEmpty(recordList)) {
            return refundMoney.toString();
        }
        for (AuthorizeOrderRecord record : recordList) {
            if (record.isWait()) {
                refundMoney = refundMoney.add(new BigDecimal(record.getMoney()));
            }
        }
        return refundMoney.toString();
    }

    /**
     * 创建预支付费用详情
     *
     * @param order
     * @return
     */
    private void createOrderFee(AuthorizeOrder order) {
        String fee = authorizeFeeService.queryByAppId(order.getAppId());
        String serviceFee = MoneyUtil.multiply(fee,order.getMoney());
        AuthorizeOrderFee orderFee = new AuthorizeOrderFee();
        orderFee.setMoney(order.getMoney());
        orderFee.setServiceMoney(serviceFee);
        orderFee.setOrderRealMoney(order.getMoney());
//        orderFee.setOrderRealMoney(MoneyUtil.add(order.getMoney(),serviceFee));
        orderFee.setOrderNo(order.getOrderNo());
        orderFee.setRequestNo(order.getRequestNo());
        order.setFee(orderFee);
    }



    /**
     * 获取超时时间
     *
     * @return
     */
    private String getTimeOut() {
        return prePay;
    }


    /**
     * 获取拓展信息
     *
     * @return
     */
    private ExtendParam getExtendParam() {
        ExtendParam param = new ExtendParam();
        param.setCategory("HOTEL");
        return param;
    }

    /**
     * 获取结算时间
     *
     * @return
     */
    private String getSettleDate() {
        return AuthorizeUtil.getSettleDate();
    }

    /**
     * 获取支持的付款方式
     *
     * @param payWay
     * @return
     */
    private Integer getPayWay(Integer payWay) {
        return DataUtil.isEmpty(payWay) ? AuthorizeOrder.PayWay.ALL.getCode() : payWay;
    }
}

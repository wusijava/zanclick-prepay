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
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.authorize.vo.*;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

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
    public PayResult prePay(AuthorizePay dto) {
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
        result.setOrderNo(order.getOrderNo());
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(order.getConfigurationId());
        AlipayClient client = authorizeConfigurationService.getAlipayClient(configuration);
        AuthorizeDTO payModal = createPayModal(order);
        payModal.setPayee_user_id(configuration.getIsvUid());
        AlipayFundAuthOrderVoucherCreateResponse createResponse = AuthorizePayUtil.qrFreeze(client, NOTIFY_URL, payModal);
        if (createResponse.isSuccess()) {
            order.setQrCodeUrl(createResponse.getCodeValue());
            result.setQrCodeUrl(order.getQrCodeUrl());
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
        result.setOrderNo(dto.getOrderNo());
        AuthorizeOrder order = queryByOrderNoOrOutTradeNo(dto.getOrderNo(), dto.getOutTradeNo());
        if (order == null || !order.isPayed()) {
            String message = order == null ? "交易订单号有误" : "订单状态异常，无法结算";
            result.setMessage(message);
            result.setFail();
            return result;
        }
        if(MoneyUtil.largeMoney(dto.getAmount(),order.getMoney())){
            result.setMessage("结算金额超出限制");
            result.setFail();
            return result;
        }
        SupplyChainTrade trade = supplyChainTradeService.queryByAuthNo(order.getAuthNo());
        if (trade != null && !trade.isFail()) {
            result.setMessage("已结算或正在结算中");
            result.setFail();
            return result;
        }
        trade = createSupplyChainTrade(dto,order);
        if (trade.isFail()) {
            result.setMessage(trade.getFailReason());
            result.setFail();
            return result;
        }
        result.setSuccess();
        return result;
    }

    @Override
    public RefundResult refund(Refund dto) {
        RefundResult result = new RefundResult();
        String check = dto.check();
        if (check != null) {
            result.setFail();
            result.setMessage(check);
            return result;
        }
        String desc = dto.isUnFree() ? "解冻" : "转支付";
        result.setOutTradeNo(dto.getOutTradeNo());
        result.setOrderNo(dto.getOrderNo());
        AuthorizeOrder order = queryByOrderNoOrOutTradeNo(dto.getOrderNo(), dto.getOutTradeNo());
        if (order == null || order.isUnPay()) {
            String message = order == null ? "交易订单号有误" : "订单状态异常，无法进行" + desc;
            result.setMessage(message);
            result.setFail();
            return result;
        }
        String refundMoney = MoneyUtil.formatMoney(order.getRefundMoney());
        refundMoney = MoneyUtil.add(refundMoney,dto.getAmount());
        if (MoneyUtil.largeMoney(refundMoney,order.getMoney())){
            result.setMessage(desc+"金额超出限制");
            result.setFail();
            return result;
        }
        AuthorizeRefundOrder refundOrder = authorizeRefundOrderService.queryByOutRequestNo(dto.getOutRequestNo());
        if (refundOrder == null){
            refundOrder = authorizeRefundOrderService.createRefundOrder(dto.getAmount(),order.getOrderNo(),dto.getOutRequestNo(),order.getAuthNo(),dto.getType(),dto.getReason());
        }else {
            if (refundOrder.isSuccess()){
                result.setRequestNo(refundOrder.getRequestNo());
                result.setOutRequestNo(refundOrder.getOutRequestNo());
                result.setMessage("解冻成功");
                result.setSuccess();
                return result;
            }
            if (refundOrder.isWait()){
                result.setMessage("解冻中，请稍后");
                result.setFail();
                return result;
            }
            refundOrder.setAmount(dto.getAmount());
            refundOrder.setRefundReason(dto.getReason());
            refundOrder.setType(dto.getType());
        }
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryById(order.getConfigurationId());
        AlipayClient client = authorizeConfigurationService.getAlipayClient(configuration);
        if (dto.isUnFree()) {
            AlipayFundAuthOrderUnfreezeResponse unfreezeResponse = createUnFreeze(refundOrder, client);
            if (!(unfreezeResponse.isSuccess() && unfreezeResponse.getStatus().equals(Constants.TRADE_SUCCESS))) {
                refundOrder.setReason(unfreezeResponse.getSubMsg());
                authorizeRefundOrderService.refundFail(refundOrder);
                result.setMessage(unfreezeResponse.getSubMsg());
                result.setFail();
                return result;
            }
        } else {
            AlipayTradePayResponse payResponse = createPay(refundOrder, client, order.getBuyerId(),configuration.getIsvAppId(),configuration.getServiceProviderId());
            if (!payResponse.isSuccess()) {
                refundOrder.setReason(payResponse.getSubMsg());
                authorizeRefundOrderService.refundFail(refundOrder);
                result.setMessage(payResponse.getSubMsg());
                result.setFail();
                return result;
            }
        }
        //TODO 这里需要处理一下金额信息
        authorizeRefundOrderService.refundSuccess(refundOrder);
        if (MoneyUtil.equal(refundMoney,order.getMoney())){
            order.setState(AuthorizeOrder.State.settled.getCode());
            authorizeOrderService.handleAuthorizeOrder(order);
        }
        result.setSuccess();
        result.setRequestNo(refundOrder.getRequestNo());
        result.setOutRequestNo(refundOrder.getOutRequestNo());
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
        result.setTradeNo(dto.getOrderNo());
        AuthorizeOrder order = queryByOrderNoOrOutTradeNo(dto.getOrderNo(), dto.getOutTradeNo());
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
        result.setTradeNo(dto.getOrderNo());
        AuthorizeOrder order = queryByOrderNoOrOutTradeNo(dto.getOrderNo(), dto.getOutTradeNo());
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
    public PayRefundResult payRefund(PayRefundDTO dto) {
        PayRefundResult result = new PayRefundResult();
        String check = dto.check();
        if (check != null) {
            result.setMessage(check);
            result.setFail();
            return result;
        }
        AuthorizeOrder order = queryByOrderNoOrOutTradeNo(dto.getOrderNo(), dto.getOutTradeNo());
        if (order == null || order.isFail() || order.isUnPay()) {
            result.setMessage("订单信息有误");
            result.setFail();
            return result;
        }
        AuthorizeRefundOrder refundOrder = queryByRequestNoOrOutRequestNo(dto.getRequestNo(),dto.getOutRequestNo());
        if (refundOrder == null) {
            result.setMessage("订单信息有误");
            result.setFail();
            return result;
        }
        if (!refundOrder.isSuccess()){
            result.setMessage("订单状态异常");
            result.setFail();
            return result;
        }
        if (AuthorizeRefundOrder.Type.UN_FREEZE.getCode().equals(refundOrder.getState())){
            result.setMessage("解冻订单无法退款");
            result.setFail();
            return result;
        }
        AuthorizeOrderRefundRecord record = authorizeOrderRefundRecordService.queryByOutRefundNo(dto.getOutRefundNo());
        if (record == null){
            record = authorizeOrderRefundRecordService.createRefundOrder(refundOrder.getAmount(),refundOrder.getRequestNo(),dto.getOutRefundNo(),dto.getReason());
        }else {
            if (record.isSuccess()) {
                result.setAmount(record.getAmount());
                result.setIsChange("N");
                result.setOutTradeNo(order.getOutTradeNo());
                result.setOrderNo(order.getOrderNo());
                result.setRequestNo(refundOrder.getRequestNo());
                result.setOutRequestNo(refundOrder.getOutRequestNo());
                result.setRefundNo(record.getRefundNo());
                result.setOutRefundNo(record.getOutRefundNo());
                result.setRefundAmount(record.getAmount());
                result.setRefundTime(record.getCreateTime());
                result.setSuccess();
                return result;
            }
            if (record.isWait()) {
                result.setFail();
                result.setMessage("退款中");
                return result;
            }
        }
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setOut_request_no(record.getRefundNo());
        modal.setOut_trade_no(record.getRequestNo());
        modal.setRefund_amount(record.getAmount());
        AlipayTradeRefundResponse response = AuthorizePayUtil.payRefund(client, modal);
        if (!response.isSuccess()) {
            record.setReason(response.getSubMsg());
            authorizeOrderRefundRecordService.refundFail(record);
            result.setFail();
            result.setMessage(response.getSubMsg());
            return result;
        }
        authorizeOrderRefundRecordService.refundSuccess(record);
        authorizeRefundOrderService.refund(refundOrder);
        result.setAmount(record.getAmount());
        result.setIsChange("Y");
        result.setOutTradeNo(order.getOutTradeNo());
        result.setOrderNo(order.getOrderNo());
        result.setRequestNo(refundOrder.getRequestNo());
        result.setOutRequestNo(refundOrder.getOutRequestNo());
        result.setRefundNo(record.getRefundNo());
        result.setOutRefundNo(record.getOutRefundNo());
        result.setRefundAmount(record.getAmount());
        result.setRefundTime(record.getCreateTime());
        result.setSuccess();
        return result;
    }



    /**
     * 创建解冻订单
     *
     * @param order   订单详情
     * @param client  支付配置
     * @return
     */
    private AlipayFundAuthOrderUnfreezeResponse createUnFreeze(AuthorizeRefundOrder order, AlipayClient client) {
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setAuth_no(order.getAuthNo());
        modal.setOut_request_no(order.getRequestNo());
        modal.setAmount(order.getAmount());
        modal.setRemark(order.getRefundReason());
        return AuthorizePayUtil.unFreeze(client, modal);
    }

    /**
     * 创建转支付订单
     *
     * @param order  预授权订单
     * @param buyerId    请求参数
     * @param sellerId  金额
     * @param client 支付配置
     * @param serviceProviderId
     * @return
     */
    private AlipayTradePayResponse createPay(AuthorizeRefundOrder order, AlipayClient client , String buyerId, String sellerId,String serviceProviderId) {
        AuthorizeDTO modal = new AuthorizeDTO();
        modal.setAuth_no(order.getAuthNo());
        modal.setSeller_id(sellerId);
        modal.setOut_trade_no(order.getRequestNo());
        modal.setAuth_confirm_mode("NOT_COMPLETE");
        modal.setProduct_code("PRE_AUTH");
        modal.setBuyer_id(buyerId);
        modal.setTotal_amount(order.getAmount());
        modal.setSubject(order.getRefundReason());
        modal.setScene("bar_code");
        ExtendParam param = new ExtendParam();
        param.setSys_service_provider_id(serviceProviderId);
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
        dto.setAmount(order.getMoney());
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
    private AuthorizeOrder createAuthorizeOrder(AuthorizePay dto, AuthorizeMerchant merchant) {
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        AuthorizeOrder order = new AuthorizeOrder();
        order.setMoney(dto.getAmount());
        order.setOutTradeNo(dto.getOutOrderNo());
        order.setMerchantNo(dto.getMerchantNo());
        order.setTimeout(getTimeOut());
        order.setTitle(dto.getDesc());
        order.setFee(dto.getFee());
        order.setNum(dto.getNum());
        order.setStoreNo(merchant.getStoreNo());
        order.setDealType(AuthorizeOrder.DealType.SCAN.getCode());
        order.setPayWay(getPayWay(dto.getPayWay()));
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
        authorizeOrderService.insert(order);
        return order;
    }


    /**
     * 根据订单号查询或者屋外部订单号查询
     *
     * @param orderNo
     * @param outTradeNo
     */
    private AuthorizeOrder queryByOrderNoOrOutTradeNo(String orderNo, String outTradeNo) {
        AuthorizeOrder order = null;
        if (orderNo != null) {
            order = authorizeOrderService.queryByOrderNo(orderNo);
        }
        if (order == null && outTradeNo != null) {
            order = authorizeOrderService.queryByOutTradeNo(outTradeNo);
        }
        return order;
    }

    /**
     * 根据订单号查询或者屋外部订单号查询
     *
     * @param requestNo
     * @param outRequestNo
     */
    private AuthorizeRefundOrder queryByRequestNoOrOutRequestNo(String requestNo, String outRequestNo) {
        AuthorizeRefundOrder order = null;
        if (requestNo != null) {
            order = authorizeRefundOrderService.queryByRequestNo(requestNo);
        }
        if (order == null && outRequestNo != null) {
            order = authorizeRefundOrderService.queryByOutRequestNo(outRequestNo);
        }
        return order;
    }


    /**
     * 创建网商垫资订单
     *
     * @param order
     * @return
     */
    private SupplyChainTrade createSupplyChainTrade(SettleDTO dto, AuthorizeOrder order) {
        SupplyChainTrade trade = new SupplyChainTrade();
        trade.setAmount(dto.getAmount());
        trade.setAuthNo(order.getAuthNo());
        trade.setCreateTime(new Date());
        trade.setFqNum(order.getNum());
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
        trade.setConfigurationId(order.getConfigurationId());
        trade.setFreezeDate(order.getFinishTime());
        trade.setExpireDate(DateUtil.addTime(order.getFinishTime(),3,Calendar.YEAR));
        trade.setMybankFee(order.getFee());
        myBankSupplyChainService.tradeCreate(trade);
        return trade;
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
     * 获取支持的付款方式
     *
     * @param payWay
     * @return
     */
    private Integer getPayWay(Integer payWay) {
        return DataUtil.isEmpty(payWay) ? AuthorizeOrder.PayWay.ALL.getCode() : payWay;
    }
}

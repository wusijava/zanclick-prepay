package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.AlipayClient;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderFee;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRecord;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderMapper;
import com.zanclick.prepay.authorize.dto.AuthorizeDTO;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderFeeService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderRecordService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.authorize.util.AuthorizePayUtil;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 预授权订单
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public class AuthorizeOrderServiceImpl extends BaseMybatisServiceImpl<AuthorizeOrder, Long> implements AuthorizeOrderService {

    @Autowired
    private AuthorizeOrderMapper authorizeOrderMapper;
    @Autowired
    private AuthorizeOrderFeeService authorizeOrderFeeService;
    @Autowired
    private AuthorizeOrderRecordService authorizeOrderRecordService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;

    @Override
    protected BaseMapper<AuthorizeOrder, Long> getBaseMapper() {
        return authorizeOrderMapper;
    }

    @Override
    public AuthorizeOrder queryById(Long id) {
        AuthorizeOrder order = getBaseMapper().selectById(id);
        if (order != null) {
            AuthorizeOrderFee fee = authorizeOrderFeeService.queryByRequestNo(order.getRequestNo());
            order.setFee(fee);
        }
        return order;
    }

    @Override
    public AuthorizeOrder insert(AuthorizeOrder entity) {
        authorizeOrderFeeService.insert(entity.getFee());
        getBaseMapper().insert(entity);
        return entity;
    }

    @Override
    public AuthorizeOrder queryByRequestNo(String requestNo) {
        AuthorizeOrder order = authorizeOrderMapper.selectByRequestNo(requestNo);
        if (order != null) {
            AuthorizeOrderFee fee = authorizeOrderFeeService.queryByRequestNo(requestNo);
            order.setFee(fee);
        }
        return order;
    }

    @Override
    public AuthorizeOrder queryByOrderNo(String orderNo) {
        AuthorizeOrder order = authorizeOrderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            AuthorizeOrderFee fee = authorizeOrderFeeService.queryByOrderNo(orderNo);
            order.setFee(fee);
        }
        return order;
    }

    @Override
    public AuthorizeOrder queryByOutTradeNo(String outTradeNo) {
        AuthorizeOrder order = authorizeOrderMapper.selectByOutTradeNo(outTradeNo);
        if (order != null) {
            AuthorizeOrderFee fee = authorizeOrderFeeService.queryByRequestNo(order.getRequestNo());
            order.setFee(fee);
        }
        return order;
    }

    @Override
    public AuthorizeOrder queryByAuthNo(String authNo) {
        AuthorizeOrder order = authorizeOrderMapper.selectByAuthNo(authNo);
        if (order != null) {
            AuthorizeOrderFee fee = authorizeOrderFeeService.queryByRequestNo(order.getRequestNo());
            order.setFee(fee);
        }
        return order;
    }

    @Override
    public void handleAuthorizeOrder(AuthorizeOrder order) {
        //TODO 这里需要加上各个业务场景通知
        if (order.isFail()) {
            order.setFinishTime(new Date());
            this.updateById(order);
        }
        if (order.isPayed()) {
            //todo 冻结成功通知
            order.setFinishTime(new Date());
            this.updateById(order);
        }
        if (order.isSettling()) {
            //TODO 这打款成功才会生成还款账单,这个状态时由网商打款成功变化而来
            List<AuthorizeOrderRecord> recordList = authorizeOrderRecordService.queryByAuthNo(order.getAuthNo());
            if (DataUtil.isEmpty(recordList)) {
                authorizeOrderRecordService.createAuthorizeOrderRecord(order);
            }
            this.updateById(order);
        }
        if (order.isSettled()) {
            this.updateById(order);
        }
        if (order.isRefund()) {
            this.updateById(order);
        }

        if (order.isUnPay()) {
            return;
        }
        if (order.isPayed() || order.isFail()) {
        }
    }

    @Override
    public boolean maintainAuthorizeOrder(AuthorizeOrder order) {
        Long second = DateUtil.nowTimeDifference(order.getCreateTime());
        if (second >= getSeconds(order.getTimeout())) {
            AuthorizeDTO dto = new AuthorizeDTO();
            dto.setOut_order_no(order.getOrderNo());
            dto.setOut_request_no(order.getRequestNo());
            dto.setRemark("超时撤销");
            boolean cancel = AuthorizePayUtil.cancel(getAlipayClient(order), dto);
            if (cancel) {
                order.setState(AuthorizeOrder.State.closed.getCode());
                handleAuthorizeOrder(order);
                return true;
            }
        }
        return false;
    }



    /**
     * 获取超时时间的秒数(加30S)
     *
     * @param time (订单超时时间)
     */
    private Long getSeconds(String time) {
        Long seconds = 60L;
        Long timeout = Long.valueOf(time) * seconds;
        return timeout + 30;
    }

    /**
     * 获取支付配置相关
     *
     * @param order
     * @return
     */
    private AlipayClient getAlipayClient(AuthorizeOrder order) {
        AlipayClient client = authorizeConfigurationService.queryAlipayClientById(order.getConfigurationId());
        return client;
    }

}

package com.zanclick.prepay.authorize.service.impl;

import com.alipay.api.AlipayClient;
import com.zanclick.prepay.authorize.vo.AuthorizeDTO;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderMapper;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.authorize.util.AuthorizePayUtil;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private AuthorizeConfigurationService authorizeConfigurationService;

    @Override
    protected BaseMapper<AuthorizeOrder, Long> getBaseMapper() {
        return authorizeOrderMapper;
    }

    @Override
    public AuthorizeOrder queryById(Long id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    public AuthorizeOrder insert(AuthorizeOrder entity) {
        getBaseMapper().insert(entity);
        return entity;
    }

    @Override
    public AuthorizeOrder queryByRequestNo(String requestNo) {
        return authorizeOrderMapper.selectByRequestNo(requestNo);
    }

    @Override
    public AuthorizeOrder queryByOrderNo(String orderNo) {
        return authorizeOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public AuthorizeOrder queryByOutTradeNo(String outTradeNo) {
        return authorizeOrderMapper.selectByOutTradeNo(outTradeNo);
    }

    @Override
    public AuthorizeOrder queryByAuthNo(String authNo) {
        return authorizeOrderMapper.selectByAuthNo(authNo);
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

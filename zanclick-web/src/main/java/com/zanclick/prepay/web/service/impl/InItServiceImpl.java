package com.zanclick.prepay.web.service.impl;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.query.PayOrderQuery;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.user.entity.User;
import com.zanclick.prepay.user.query.UserQuery;
import com.zanclick.prepay.user.service.UserService;
import com.zanclick.prepay.web.service.InItService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-11-22 10:55:49
 */
@Slf4j
@Service
public class InItServiceImpl implements InItService {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private PayOrderService payOrderService;

    @Override
    public List<RegisterMerchant> initData() {
        List<RegisterMerchant> registerMerchantList = new ArrayList<>();
        AuthorizeMerchantQuery query = new AuthorizeMerchantQuery();
        query.setState(AuthorizeMerchant.State.success.getCode());
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        for (AuthorizeMerchant merchant : merchantList) {
            if (DataUtil.isNotEmpty(merchant.getUid())){
                continue;
            }
            try {
                RegisterMerchant registerMerchant = initMerchant(merchant);
                if (registerMerchant != null){
                    registerMerchantList.add(registerMerchant);
                }
            } catch (Exception e) {
                log.error("商户初始化错误:{}", merchant.getWayId(), e);
            }
        }
        System.err.println("--------------初始化结束----------------");
        return registerMerchantList;
    }


    @Transactional(rollbackFor = Exception.class)
    public RegisterMerchant initMerchant(AuthorizeMerchant merchant) {
        User user = userService.createUser(merchant.getSellerNo(), merchant.getStoreSubjectName(), merchant.getStoreName(), merchant.getWayId(), merchant.getContactPhone());
        if (user == null){
            log.error("重复数据:{}",merchant.getWayId());
            return null;
        }
        log.error("开始处理:{}",merchant.getWayId());
        merchant.setUid(user.getUid());
        merchant.setStoreMarkCode(user.getStoreMarkCode());
        authorizeMerchantService.updateById(merchant);
        PayOrderQuery query = new PayOrderQuery();
        query.setWayId(merchant.getWayId());
        List<PayOrder> orderList = payOrderService.queryList(query);
        for (PayOrder order : orderList) {
            order.setUid(merchant.getUid());
            order.setStoreMarkCode(merchant.getStoreMarkCode());
            payOrderService.updateById(order);
        }
        RegisterMerchant registerMerchant = getRegisterMerchant(merchant);
        registerMerchant.setPassword(user.getPwd());
        return registerMerchant;
    }

    /**
     * 调整
     *
     * @param dto
     */
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
}

package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import com.zanclick.prepay.authorize.mapper.RedPackBlacklistMapper;
import com.zanclick.prepay.authorize.service.RedPackBlacklistService;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author panliang
 * @Date 2019/11/22 11:27
 * @Description //
 **/
@Slf4j
@Service
public class RedPackBlacklistServiceImpl extends BaseMybatisServiceImpl<RedPackBlacklist, Long> implements RedPackBlacklistService {

    @Autowired
    private RedPackBlacklistMapper redPackBlacklistMapper;

    @Override
    protected BaseMapper<RedPackBlacklist, Long> getBaseMapper() {
        return redPackBlacklistMapper;
    }

    @Override
    public RedPackBlacklist querySellerNo(String sellerNo) {
        return redPackBlacklistMapper.selectBySellerNo(sellerNo);
    }
}

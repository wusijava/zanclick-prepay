package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import com.zanclick.prepay.common.base.service.BaseService;

public interface RedPackBlacklistService extends BaseService<RedPackBlacklist, Long> {

    /**
     * 查询支付宝账号
     *
     * @param sellerNo
     * @return
     */
    RedPackBlacklist querySellerNo(String sellerNo);
}

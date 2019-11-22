package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;

public interface RedPackBlacklistMapper extends BaseMapper<RedPackBlacklist, Long> {

    /**
     * 根据支付宝账号查询
     *
     * @param sellerNo
     * @return
     */
    RedPackBlacklist selectBySellerNo(String sellerNo);
}

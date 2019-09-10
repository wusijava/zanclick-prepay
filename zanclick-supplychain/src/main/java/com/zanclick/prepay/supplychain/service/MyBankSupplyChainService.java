package com.zanclick.prepay.supplychain.service;

import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;


/**
 * 网商垫资相关操作
 *
 * @author duchong
 * @date 2019-7-15 18:26:40
 **/

public interface MyBankSupplyChainService {

    /**
     * 网商交易垫资创建
     *
     * @param trade
     */
    void tradeCreate(SupplyChainTrade trade);

    /**
     * 网商垫资交易创建撤销
     *
     * @param auth_no
     * @return
     */
    String tradeCancel(String auth_no);


    /**
     * 查询还款方案
     *
     * @param auth_no
     */
    void tradeRepay(String auth_no);

}

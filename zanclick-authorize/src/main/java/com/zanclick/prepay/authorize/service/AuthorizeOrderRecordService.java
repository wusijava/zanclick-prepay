package com.zanclick.prepay.authorize.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRecord;

import java.util.List;

/**
 * 预授权订单预期还款记录
 *
 * @author duchong
 * @date 2019-7-8 15:27:11
 **/
public interface AuthorizeOrderRecordService extends BaseService<AuthorizeOrderRecord, Long> {


    /**
     * 根据授权订单号
     *
     * @param authNo
     * @return
     */
    List<AuthorizeOrderRecord> queryByAuthNo(String authNo);

    /**
     * 根据授权订单号
     *
     * @param authNo
     * @return
     */
    List<AuthorizeOrderRecord> queryWaitByAuthNo(String authNo);

    /**
     * 根据订单号查询
     *
     * @param tradeNo
     * @return
     */
    List<AuthorizeOrderRecord> queryByTradeNo(String tradeNo);


    /**
     * 根据授权订单号查询
     *
     * @param authNo
     * @param num
     * @return
     */
    List<AuthorizeOrderRecord> queryByAuthNoAndNum(String authNo, Integer num);


    /**
     * 根据授权订单号查询
     *
     * @param recordList
     * @param type 0解冻 1转支付
     * @param tradeNo
     * @return
     */
    void handleRecordList(List<AuthorizeOrderRecord> recordList, Integer type, String tradeNo);


    /**
     * 查询这一期是否为最后一期
     *
     * @param authNo
     * @param num
     * @return
     */
    boolean queryListRecord(String authNo, Integer num);

    /**
     * 创建预授权账单
     *
     * @param order
     * @return
     */
    List<AuthorizeOrderRecord> createAuthorizeOrderRecord(AuthorizeOrder order);

}

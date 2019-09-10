package com.zanclick.prepay.authorize.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRecord;

import java.util.List;
import java.util.Map;

/**
 * 直付通预授权订单
 *
 * @author duchong
 * @date 2019-5-22 03:18:13
 */
public interface AuthorizeOrderRecordMapper extends BaseMapper<AuthorizeOrderRecord, Long> {

    /**
     * 根据授权订单号
     *
     * @param authNo
     * @return
     */
    List<AuthorizeOrderRecord> selectByAuthNo(String authNo);

    /**
     * 根据授权订单号查询
     *
     * @param params
     * @return
     */
    List<AuthorizeOrderRecord> selectByAuthNoAndNum(Map<String, Object> params);

    /**
     * 查询最大的期数
     *
     * @param authNo
     * @return
     */
    Integer selectMaxNum(String authNo);

}

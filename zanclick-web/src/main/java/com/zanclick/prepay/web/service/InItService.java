package com.zanclick.prepay.web.service;

import com.zanclick.prepay.authorize.vo.RegisterMerchant;

import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-11-22 10:55:49
 */
public interface InItService {

    /**
     * 初始化数据信息
     */
    List<RegisterMerchant> initData();
}

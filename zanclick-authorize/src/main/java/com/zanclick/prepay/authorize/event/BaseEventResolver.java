package com.zanclick.prepay.authorize.event;

import com.zanclick.prepay.authorize.exception.SupplyChainException;

/**
 * @author lvlu
 * @date 2019-05-10 11:57
 **/
public interface BaseEventResolver {

    /**
     * 网商垫资异步结果消息业务处理
     * @param dataObject
     * @throws SupplyChainException
     */
    void resolveEvent(String dataObject) throws SupplyChainException;

}

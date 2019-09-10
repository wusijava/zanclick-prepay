package com.zanclick.prepay.supplychain.event.domain;

import lombok.Data;

/**
 * @author lvlu
 * @date 2019-05-10 12:09
 **/
@Data
public abstract class BaseEvent {

    private String requestId;

    private String dataOrgId;

    private String ipId;

    private String ipRoleId;

    private String outOrderNo;

    private String success;

    private String errorCode;

    private String errorMsg;
}

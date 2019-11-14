package com.zanclick.prepay.authorize.vo;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-11-14 16:53:41
 */
@Data
public class PayeeInfo {

    private String identity;

    private String identity_type;

    private String name;
}

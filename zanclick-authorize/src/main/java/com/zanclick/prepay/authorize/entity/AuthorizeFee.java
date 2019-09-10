package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 网商预授权垫资费率
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class AuthorizeFee implements Identifiable<Long> {

    private Long id;

    /**
     * 冻结金额
     **/
    private String appId;


    private String fee;
}

package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019/12/7 10:07
 */
@Data
public class AuthorizeBudget implements Identifiable<Long> {

    private Long id;

    private String budgetNo;

    private String authNo;

    private String subject;

    private String rpyDate;

    private String rpyAmount;

    private Integer rpyNum;

    private String buyerId;

    private String sellerId;

    private Date createTime;

    private Long configurationId;

    private Integer status;

    private String wsRpyAmount;

    private Date wsRpyDate;

    private Date wsDueDate;

    private String merchantNo;

}

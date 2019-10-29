package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author lvlu
 * @date 2019-05-10 14:53
 **/
@Data
public class SupplyChainBill implements Identifiable<Long> {

    private Long id;
    private String authNo;
    private String amount;
    private String installNum;
    private Date startDate;
    private Date dueDate;
    private String fee;
    private String totalAmount;
    private Date createTime;
    private String requestId;
    private Integer state;

    private String failReason;



}

package com.zanclick.prepay.supplychain.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import com.zanclick.prepay.supplychain.enums.BillStateEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019-05-10 14:53
 **/
@Data
public class SupplyChainBill implements Identifiable<Long> {

    private Long id;
    private String authNo;
    private String amount;
    private String installNum;
    private Date start_date;
    private Date due_date;
    private String fee;
    private String totalAmount;
    private Date createTime;
    private String requestId;
    private BillStateEnum state;
    private String failReason;



}

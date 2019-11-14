package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class RedPacket implements Identifiable<Long> {

  private Long id;
  private String amount;
  private String orderNo;
  private Date createTime;
  private String receiveNo;
  private String appId;
  private Integer state;
  private String reason;
  private String wayId;
  private String sellerNo;






}

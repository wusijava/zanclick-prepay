package com.zanclick.prepay.order.entity;


import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;
@Data
public class RedPackDetail implements Identifiable<Long> {

  private long id;
  private String outOrderNo;
  private String amount;
  private String settleAmount;
  private Integer num;
  private String redPackAmount;
  private Integer state;
  private String sellerNo;
  private Date createTime;
  private Date updateTime;


  @Override
  public Long getId() {
    return null;
  }

  @Override
  public void setId(Long aLong) {

  }
}

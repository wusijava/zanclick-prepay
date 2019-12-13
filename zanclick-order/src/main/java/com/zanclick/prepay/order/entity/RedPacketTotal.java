package com.zanclick.prepay.order.entity;


import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;
@Data
public class RedPacketTotal implements Identifiable<Long> {

  private long id;
  //收款人姓名
  private String name;
  //收款人账号
  private String sellerNo;
  //收款类型 个人 日结 周结 月结
  private long type;
  //创建时间
  private Date creatTime;
  //未结算单数
  private long unsettleNum;
  //未结算金额
  private Double unsettleAmount;
  //实际结算单数
  private long realitySettleNum;
  //实际结算金额
  private long realitySettleAmount;
  //是否结算 默认1结算  0不结算
  private long state;


  @Override
  public Long getId() {
    return null;
  }

  @Override
  public void setId(Long aLong) {

  }

  public String getName() {
    return name;
  }

  public String getSellerNo() {
    return sellerNo;
  }

  public long getType() {
    return type;
  }

  public Date getCreatTime() {
    return creatTime;
  }

  public long getUnsettleNum() {
    return unsettleNum;
  }

  public Double getUnsettleAmount() {
    return unsettleAmount;
  }

  public long getRealitySettleNum() {
    return realitySettleNum;
  }

  public long getRealitySettleAmount() {
    return realitySettleAmount;
  }

  public long getState() {
    return state;
  }
}

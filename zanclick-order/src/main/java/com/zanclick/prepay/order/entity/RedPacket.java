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


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }


  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }


  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getReceiveNo() {
    return receiveNo;
  }

  public void setReceiveNo(String receiveNo) {
    this.receiveNo = receiveNo;
  }


  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }


  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }


  public String getWayId() {
    return wayId;
  }

  public void setWayId(String wayId) {
    this.wayId = wayId;
  }


  public String getSellerNo() {
    return sellerNo;
  }

  public void setSellerNo(String sellerNo) {
    this.sellerNo = sellerNo;
  }

}

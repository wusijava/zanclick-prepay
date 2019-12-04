package com.zanclick.prepay.order.entity;


import com.zanclick.prepay.common.entity.Identifiable;

public class Area  implements Identifiable<Long> {

  private long id;
  private String name;
  private String level;
  private String code;
  private String parentCode;


  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long aLong) {

  }

  public void setId(long id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public void setLevel(String level) {
    this.level = level;
  }

  public String getLevel() {
    return level;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }


  public String getParentCode() {
    return parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }

}

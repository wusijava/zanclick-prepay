package com.zanclick.prepay.order.entity;


import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

@Data
public class Area implements Identifiable<Long> {

  private Long id;

  private String name;

  private Integer level;

  private String code;

  private String parentCode;

}

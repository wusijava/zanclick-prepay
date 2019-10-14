package com.zanclick.prepay.app.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author zanclick
 * @date 2019-10-14 15:33:03
 **/
@Data
public class AppSftpConfig implements Identifiable<Long> {

     private Long id;

     private String appId;

     private String host;

     private Integer port;

     private String username;

     private String password;

     private String remoteDir;

     private String localDir;

     /**
      * 类型，1为套餐同步配置，其他类型按需添加
      */
     private Integer type;

     private String desc;

     /**
      * 状态 1为可用，其他待定
      */
     private Integer state;

     private Date createTime;

     private Date modifyTime;

}

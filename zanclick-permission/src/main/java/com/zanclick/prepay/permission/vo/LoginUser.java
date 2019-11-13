package com.zanclick.prepay.permission.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lvlu
 * @date 2019-03-06 17:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements Serializable {

    private String id;

    private String username;

    private String password;

    private String nickName;

    private Integer type;

    private String uid;

    private String salt;

    private String mobile;
}

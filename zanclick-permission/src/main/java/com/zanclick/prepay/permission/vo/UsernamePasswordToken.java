package com.zanclick.prepay.permission.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lvlu
 * @date 2019-03-06 17:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordToken {
    private String username;
    private String password;
}

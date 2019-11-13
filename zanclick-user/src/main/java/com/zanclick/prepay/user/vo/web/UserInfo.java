package com.zanclick.prepay.user.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@ApiModel(description = "账目列表",value = "UserInfo")
@Data
public class UserInfo {

    private String username;

    private String image;

    private String mobile;

    private String uid;
}

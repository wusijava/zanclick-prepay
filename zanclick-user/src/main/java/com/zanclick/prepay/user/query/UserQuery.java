package com.zanclick.prepay.user.query;

import com.zanclick.prepay.user.entity.User;
import lombok.Data;

/**
 * @author duchong
 * @description 用户
 * @date 2019-8-3 10:05:37
 */
@Data
public class UserQuery extends User {

    private Integer page;

    private Integer limit;

    /**
     * 短信验证码
     */
    private String smsCode;

    /**
     * 新手机号
     */
    private String newMobile;

    /**
     * 新手机验证码
     */
    private String newSmsCode;
}

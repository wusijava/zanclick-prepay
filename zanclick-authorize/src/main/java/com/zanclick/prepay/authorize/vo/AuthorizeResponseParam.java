package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.common.entity.ResponseParam;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date 2019-7-11 11:17:06
 */
@Data
public class AuthorizeResponseParam extends ResponseParam {

    /**
     * 订单状态
     */
    private Integer state = 100;
}

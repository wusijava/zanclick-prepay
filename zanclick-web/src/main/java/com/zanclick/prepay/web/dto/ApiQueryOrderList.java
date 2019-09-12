package com.zanclick.prepay.web.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.PayDTO;
import com.zanclick.prepay.common.utils.AESUtil;
import lombok.Data;

/**
 * 商户信息验证
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class ApiQueryOrderList{

    /**
     * 分页参数
     */
    private Long nextIndex;
}

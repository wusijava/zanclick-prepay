package com.zanclick.prepay.authorize.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class Transfer {

    private String out_biz_no;

    private String payee_type;

    private String payee_account;

    private String amount;

    private String payer_show_name;

    private String payee_real_name;

    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

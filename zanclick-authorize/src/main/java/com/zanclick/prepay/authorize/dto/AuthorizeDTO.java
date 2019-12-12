package com.zanclick.prepay.authorize.dto;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.dto.ExtendParam;
import com.zanclick.prepay.authorize.dto.PayChannelType;
import lombok.Data;


/**
 * 资金冻结Model
 *
 * @author duchong
 * @date2018-12-4 10:31:28
 */
@Data
public class AuthorizeDTO {

    /**
     * 支付授权码
     * */
    private String auth_code;

    private String auth_code_type;

    private String out_order_no;

    private String out_request_no;

    private String order_title;

    private String amount;

    private String payee_user_id;

    private String payee_logon_id;

    private String pay_timeout;

    private String product_code;

    private ExtendParam extra_param;

    private String trans_currency;

    private String settle_currency;

    private PayChannelType[] enable_pay_channels;

    private String remark;

    private String auth_no;

    /**
     * 转支付
     * */
    private String auth_confirm_mode;

    private String subject;

    private String body;

    private String buyer_id;

    private String out_trade_no;

    private String total_amount;

    private String seller_id;

    private String scene;

    private String refund_amount;

    private String store_id;

    private String terminal_id;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}

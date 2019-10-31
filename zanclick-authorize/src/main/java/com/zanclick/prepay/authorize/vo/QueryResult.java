package com.zanclick.prepay.authorize.vo;

import com.zanclick.prepay.authorize.vo.AuthorizeResponseParam;
import lombok.Data;

/**
 * 退款数据封装
 *
 * @author duchong
 * @date 2019-7-5 10:32:07
 */
@Data
public class QueryResult extends AuthorizeResponseParam {

    /**
     * 订单号 （第三方产生）
     */
    private String outTradeNo;

    /**
     * 订单号 （自己生成）
     */
    private String tradeNo;

}

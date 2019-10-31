package com.zanclick.prepay.web;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.vo.Refund;
import com.zanclick.prepay.authorize.vo.RefundResult;
import com.zanclick.prepay.common.utils.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lvlu
 * @date 2019-07-06 14:32
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class ZftTest {

    @Autowired
    private AuthorizePayService authorizePayService;

    @Test
    public void ss(){
        Refund dto = new Refund();
        dto.setOutTradeNo("157251652940826077529");
        dto.setOutRequestNo(StringUtils.getTradeNo());
        dto.setAmount("2.00");
        dto.setType(0);
        dto.setReason("交易退款");
        RefundResult result = authorizePayService.refund(dto);
        System.err.println(JSONObject.toJSONString(result));
    }


}

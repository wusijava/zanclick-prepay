//package com.zanclick.prepay.web;
//
//import com.alibaba.fastjson.JSONObject;
//import com.zanclick.prepay.authorize.dto.RefundDTO;
//import com.zanclick.prepay.authorize.dto.RefundResult;
//import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
//import com.zanclick.prepay.authorize.pay.AuthorizePayService;
//import com.zanclick.prepay.common.utils.StringUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author lvlu
// * @date 2019-07-06 14:32
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = WebApplication.class)
//public class ZftTest {
//    @Autowired
//    private AuthorizePayService authorizePayService;
//
//    @Test
//    public void ss(){
//        RefundDTO dto = new RefundDTO();
//        dto.setRefundNo(StringUtils.getTradeNo());
//        dto.setTradeNo("156955320206100471151");
//        RefundResult result = authorizePayService.refund(dto);
//        System.err.println(JSONObject.toJSONString(result));
//    }
//
//
//}

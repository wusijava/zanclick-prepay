package com.zanclick.prepay.common.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duchong
 * @description
 * @date
 */
public class Method {


    static List<String> methods = new ArrayList<>();

    static {
        methods.add("comZanclickVerifyIdentity");
        methods.add("comZanclickCreateMerchant");
        methods.add("comZanclickQueryAuthOrderList");
        methods.add("comZanclickRefundAuthPay");
        methods.add("comZanclickQueryAuthPay");
        methods.add("comZanclickCreateAuthPrePay");
        methods.add("comZanclickQueryAuthOrderDetail");
    }

    public static boolean  hasMethod(String methodName){
        return methods.contains(methodName);
    }
}

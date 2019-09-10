package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.authorize.pay.dto.PayChannelType;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户能够选择的付款方式
 *
 * @author duchong
 * @date 2019-7-9 11:09:26
 */
public class PayWay {


    public static Map<Integer, PayChannelType[]> typeList = new HashMap<>();

    static {


        PayChannelType type1 = new PayChannelType();
        type1.setPayChannelType("PCREDIT_PAY");
        PayChannelType type2 = new PayChannelType();
        type2.setPayChannelType("MONEY_FUND");
        PayChannelType types0[] = {type1};
        typeList.put(0, types0);
        PayChannelType types1[] = {type2};
        typeList.put(1, types1);
        PayChannelType types2[] = {type1, type2};
        typeList.put(2, types2);
    }
}

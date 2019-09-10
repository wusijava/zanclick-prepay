package com.zanclick.prepay.authorize.dto;

import com.zanclick.prepay.common.entity.RequestParam;
import lombok.Data;

/**
 * 商户创建
 *
 * @author duchong
 * @date 2019-7-8 15:49:20
 */
@Data
public class MerchantUpdateDTO extends RequestParam {

    private String name;

    private String contactName;

    private String contactPhone;

    private String sellerNo;

    private String sellerId;

    private String appId;

    private String merchantNo;


    @Override
    public String check() {
        if (checkNull(appId)){
            return "缺少appId";
        }
        if (checkNull(merchantNo)){
            return "缺少商户号";
        }
        return null;
    }
}

package com.zanclick.prepay.web.dto;

import com.zanclick.prepay.common.entity.Param;
import lombok.Data;

@Data
public class QueryMerchantState extends Param {

    private String wayId;

    public String check() {
        if (checkNull(wayId)) {
            return "缺少渠道编码";
        }
        return null;
    }
}

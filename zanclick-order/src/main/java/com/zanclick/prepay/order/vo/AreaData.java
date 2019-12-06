package com.zanclick.prepay.order.vo;

import lombok.Data;

@Data
public class AreaData {

    /**
     * 名称
     */
    private String name;

    /**
     * code
     */
    private String code;

    public AreaData() {

    }

    public AreaData(String name, String code) {
        this.name = name;
        this.code = code;
    }

}

package com.zanclick.prepay.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author panliang
 * @Date 2019/12/5 15:07
 * @Description //
 **/
@Data
public class AreaWebInfo {

    private List<AreaInfo> provinceList;

    private List<AreaInfo> cityList;

    @Data
    public static class AreaInfo {
        /**
         * code
         */
        private String code;

        /**
         * 名称
         */
        private String name;

    }

}

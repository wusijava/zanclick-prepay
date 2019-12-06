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

    private List<AreaData> provinceList;

    private List<AreaData> cityList;

}

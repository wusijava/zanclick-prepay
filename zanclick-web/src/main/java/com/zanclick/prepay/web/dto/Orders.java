package com.zanclick.prepay.web.dto;

import lombok.Data;

import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-9-12 16:14:17
 */
@Data
public class Orders {

    private Integer size;

    private List<OrderList> list;
}

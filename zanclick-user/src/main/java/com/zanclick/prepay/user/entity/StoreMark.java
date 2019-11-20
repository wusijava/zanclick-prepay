package com.zanclick.prepay.user.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description 门店区别
 * @date 2019-8-3 10:05:37
 */
@Data
public class StoreMark implements Identifiable<Long> {

    private Long id;

    private String aliPayLoginNo;

    private Date createTime;

    private String code;

    private String name;
}

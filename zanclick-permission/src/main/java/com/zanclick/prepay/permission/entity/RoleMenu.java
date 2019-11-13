package com.zanclick.prepay.permission.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * @author duchong
 * @description 权限
 * @date 2019-8-3 10:05:37
 */
@Data
public class RoleMenu implements Identifiable<Long> {

    private Long id;

    private Integer type;

    private String homeMenuCode;

    private String menuCode;


}

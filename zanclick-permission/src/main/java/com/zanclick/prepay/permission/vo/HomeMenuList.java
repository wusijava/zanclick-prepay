package com.zanclick.prepay.permission.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author duchong
 * @description 主菜单
 * @date 2019-11-13 14:31:25
 */
@Data
public class HomeMenuList {

    private String title;

    private String name;

    private String icon;

    private List<MenuList> submenus;
}

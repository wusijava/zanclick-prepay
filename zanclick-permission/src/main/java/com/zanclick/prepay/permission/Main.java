package com.zanclick.prepay.permission;

import com.zanclick.prepay.common.generator.CodeGenerator;
import com.zanclick.prepay.permission.entity.Menu;
import com.zanclick.prepay.permission.entity.RoleMenu;

/**
 * @author duchong
 * @description
 * @date
 */
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Menu.class);
//        codeGenerator.generateDao(basePack, RoleMenu.class);
//        codeGenerator.generateService(basePack, RoleMenu.class);

    }
}

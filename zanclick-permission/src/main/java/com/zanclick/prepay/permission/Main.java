package com.zanclick.prepay.permission;

import com.zanclick.prepay.auth.entity.Resource;
import com.zanclick.prepay.common.generator.CodeGenerator;

/**
 * @author duchong
 * @description
 * @date
 */
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Resource.class);
//        codeGenerator.generateDao(basePack, RoleAndResource.class);
//        codeGenerator.generateService(basePack, RoleAndResource.class);

    }
}

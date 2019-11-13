package com.zanclick.prepay.user;

import com.zanclick.prepay.common.generator.CodeGenerator;
import com.zanclick.prepay.user.entity.User;

/**
 * @author duchong
 * @description
 * @date
 */
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, User.class);
//        codeGenerator.generateDao("com.zanclick.jd.user", User.class);
//        codeGenerator.generateService("com.click.jd.user.modules", User.class);
//        codeGenerator.generateCreateSqlForPackage("com.click.jd.merchant.modules");
    }
}

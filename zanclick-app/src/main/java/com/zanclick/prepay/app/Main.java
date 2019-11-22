package com.zanclick.prepay.app;

import com.zanclick.prepay.app.entity.ApiSwitch;
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
        codeGenerator.generateMybatisXml(basePack, ApiSwitch.class);
        codeGenerator.generateDao(basePack, ApiSwitch.class);
        codeGenerator.generateService(basePack, ApiSwitch.class);
    }
}

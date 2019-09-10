package com.zanclick.prepay.app;

import com.zanclick.prepay.app.entity.AppInfo;
import com.zanclick.prepay.common.generator.CodeGenerator;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        new CodeGenerator(). generateMybatisXml(basePack,AppInfo.class);
//        new CodeGenerator(). generateDao(basePack,AppInfo.class);
//        new CodeGenerator(). generateService(basePack,AppInfo.class);
    }
}

package com.zanclick.prepay.setmeal;

import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.common.generator.CodeGenerator;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {

    public static void main(String[] args) {

        String basePack = Main.class.getPackage().getName();
        CodeGenerator generator = new CodeGenerator();
//        generator.generateDao(basePack,SetMeal.class);
        generator.generateMybatisXml(basePack, SetMeal.class);
//        generator.generateService(basePack,SetMeal.class);
    }
}

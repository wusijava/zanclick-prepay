package com.zanclick.prepay.setmeal;

import com.zanclick.prepay.common.generator.CodeGenerator;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.entity.SetMealLog;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator generator = new CodeGenerator();
        generator.generateMybatisXml(basePack, SetMealLog.class);
       generator.generateDao(basePack,SetMealLog.class);
        generator.generateService(basePack,SetMealLog.class);
    }
}

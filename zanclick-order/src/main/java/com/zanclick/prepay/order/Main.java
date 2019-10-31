package com.zanclick.prepay.order;

import com.zanclick.prepay.common.generator.CodeGenerator;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.entity.SettleOrder;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator generator = new CodeGenerator();
        generator.generateMybatisXml(basePack, PayOrder.class);
//        generator.generateService(basePack,SettleOrder.class);
//        generator.generateDao(basePack,SettleOrder.class);
    }
}

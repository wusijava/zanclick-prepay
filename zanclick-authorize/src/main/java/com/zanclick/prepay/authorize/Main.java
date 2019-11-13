package com.zanclick.prepay.authorize;

import com.zanclick.prepay.authorize.entity.*;
import com.zanclick.prepay.common.generator.CodeGenerator;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {
    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator generator = new CodeGenerator();
        generator.generateMybatisXml(basePack, AuthorizeMerchant.class);
//        generator.generateDao(basePack,SupplyChainTrade.class);
//        generator.generateService(basePack,SupplyChainTrade.class);
    }
}

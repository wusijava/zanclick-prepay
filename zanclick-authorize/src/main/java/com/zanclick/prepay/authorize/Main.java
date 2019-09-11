package com.zanclick.prepay.authorize;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.common.generator.CodeGenerator;

/**
 * @author lvlu
 * @date 2019-08-29 15:13
 **/
public class Main {
    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        new CodeGenerator().generateMybatisXml(basePack, AuthorizeMerchant.class);
    }
}

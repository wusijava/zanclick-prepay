package com.zanclick.prepay.supplychain;

import com.zanclick.prepay.common.generator.CodeGenerator;
import com.zanclick.prepay.supplychain.entity.SupplyChainBill;
import com.zanclick.prepay.supplychain.entity.SupplyChainTrade;

/**
 * @author lvlu
 * @date 2019-07-04 17:49
 **/
public class Main {

    public static void main(String[] args) {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml("com.zanclick.zyjk.supplychain",SupplyChainBill.class);
        codeGenerator.generateMybatisXml("com.zanclick.zyjk.supplychain",SupplyChainTrade.class);
    }
}

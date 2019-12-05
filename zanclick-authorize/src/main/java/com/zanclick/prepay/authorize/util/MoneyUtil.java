package com.zanclick.prepay.authorize.util;

import java.math.BigDecimal;

/**
 * @author duchong
 * @date 2019-4-8 11:31:23
 * @description 金额相关
 */
public class MoneyUtil {

    /**
     * 比较金额1 是否大于 或等于金额2
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static boolean judgeMoney(String money1, String money2) {
        if (money1 == null || money2 == null) {
            return false;
        }
        Integer result =  getFormatMoney(money1).compareTo(getFormatMoney(money2));
        return result == 0 || result == 1;
    }

    public static boolean largeMoney(String money1, String money2) {
        try {
            Integer result = getFormatMoney(money1).compareTo(getFormatMoney(money2));
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static BigDecimal getFormatMoney(String total_money) {
        if (total_money == null) {
            return new BigDecimal("0.00");
        }
        BigDecimal decimal = new BigDecimal("0.00");
        try {
            decimal = decimal.add(new BigDecimal(total_money));
        } catch (Exception e) {
            return decimal;
        }
        return decimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public static boolean isNumber(String total_money) {
        try {
            new BigDecimal(total_money);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean equal(String money1, String money2) {
        Integer result =getFormatMoney(money1).compareTo(getFormatMoney(money2));
        return result == 0;
    }

    /**
     * 金额相减
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static String subtract(String money1, String money2) {
        return new BigDecimal(money1).subtract(new BigDecimal(money2)).toString();
    }

    /**
     * 金额相乘
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static String multiply(String money1, String money2) {
        BigDecimal total = new BigDecimal(money1).multiply(new BigDecimal(money2));
        return total.toString();
    }

    /**
     * 金额相加
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static String add(String money1, String money2) {
        BigDecimal total = new BigDecimal(money1).add(new BigDecimal(money2)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return total.toString();
    }

    /**
     * 金额相加
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static String divide(String money1, String money2) {
        BigDecimal total = new BigDecimal(money1).divide(new BigDecimal(money2),2, BigDecimal.ROUND_HALF_EVEN);
        return total.toString();
    }

    public static void main(String[] args) {
        System.err.println(divide("100","12"));
    }


    /**
     * 获取金额，若金额为整数，则显示整数，若金额为小数，则保留两位小数
     *
     * @param total_money
     * @return
     */
    public static String formatMoney(String total_money) {
        if (total_money == null) {
            return "0.00";
        }
        BigDecimal decimal = new BigDecimal("0");
        try {
            decimal = decimal.add(new BigDecimal(total_money));
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00";
        }
        return decimal.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
    }
}

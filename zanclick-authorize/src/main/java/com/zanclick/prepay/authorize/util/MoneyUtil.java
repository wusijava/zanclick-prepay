package com.zanclick.prepay.authorize.util;

import com.zanclick.prepay.authorize.dto.CalculateDetail;
import com.zanclick.prepay.authorize.dto.DealDetail;
import com.zanclick.prepay.common.utils.DataUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
        Integer result = new BigDecimal(money1).compareTo(new BigDecimal(money2));
        return result == 0 || result == 1;
    }

    public static boolean largeMoney(String money1, String money2) {
        if (DataUtil.isEmpty(money1)) {
            money1 = "0.00";
        }
        if (DataUtil.isEmpty(money2)) {
            money2 = "0.00";
        }
        try {
            Integer result = new BigDecimal(money1).compareTo(new BigDecimal(money2));
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取订单金额（花呗分期）
     *
     * @param money       交易金额
     * @param fq_num      分期期数
     * @param cost_rate   分期成本手续费率
     * @param curr_rate   分期手续费率
     * @param deal_rate   交易费率
     * @param seller_type 手续费承担方  0=顾客 1=商家
     */
    public static DealDetail getDetail(String money, Integer fq_num, String cost_rate, String curr_rate, String deal_rate, Integer seller_type) {
        String formatMoney = getMoney(money);
        DealDetail detail = new DealDetail();
        detail.setSeller_type(seller_type);
        detail.setDeal_money(formatMoney);
        detail.setDeal_rate(deal_rate);
        detail.setFq_num(fq_num);
        detail.setFq_rate(curr_rate);
        detail.setCost_rate(cost_rate);
        String dealFee = getMoney(new BigDecimal(detail.getDeal_money()).multiply(new BigDecimal(detail.getDeal_rate())).toString());
        detail.setDeal_fee(dealFee);
        if (detail.getFq_num() == 0 || !zeroMoney(detail.getCost_rate()) || !zeroMoney(detail.getFq_rate())) {
            return detail;
        }
        try {
            //成本费率
            BigDecimal aliRateBigDecimal = new BigDecimal(detail.getCost_rate());
            //当前费率
            BigDecimal posRateBigDecimal = new BigDecimal(detail.getFq_rate());
            //计算单位改为分
            BigDecimal cent = new BigDecimal(detail.getDeal_money()).multiply(BigDecimal.valueOf(100));
            //成本手续费
            BigDecimal ali_fq_fee = cent.multiply(aliRateBigDecimal)
                    .setScale(1, BigDecimal.ROUND_HALF_UP);
            //交易收益
            BigDecimal hb_fq_fee = cent.multiply((posRateBigDecimal.subtract(aliRateBigDecimal)))
                    .setScale(1, BigDecimal.ROUND_HALF_EVEN);
            //分期手续费
            BigDecimal pos_fee = ali_fq_fee.add(hb_fq_fee);
            BigDecimal index = new BigDecimal(100);
            if (detail.getSeller_type().equals(0)) {
                //顾客承担手续费
                BigDecimal totalMoney = cent.add(pos_fee);
                BigDecimal payMoney = totalMoney.divide(new BigDecimal(1).add(new BigDecimal(detail.getCost_rate())),
                        2,
                        BigDecimal.ROUND_HALF_EVEN);
                detail.setFq_fee(getMoney(pos_fee.divide(index).toString()));
                detail.setCustomer_real_money(getMoney((totalMoney.divide(index)).add(new BigDecimal(dealFee)).toString()));
                detail.setBusiness_real_money(getMoney(cent.divide(index).toString()));
                String total_money = getMoney(payMoney.divide(index).toString());
                detail.setFq_profit_money(getMoney(new BigDecimal(total_money)
                        .subtract(cent.divide(index))
                        .toString()));
                detail.setOrder_real_money(total_money);
                detail.setCost_fee(getMoney(payMoney.multiply(aliRateBigDecimal)
                        .divide(index)
                        .toString()));
                BigDecimal fq_eachDB = totalMoney.divide(new BigDecimal(detail.getFq_num()), 4, RoundingMode.HALF_EVEN);
                detail.setFq_each_money(getMoney(fq_eachDB.divide(index).toString()));
                BigDecimal first_each = totalMoney.subtract(fq_eachDB.multiply(new BigDecimal(detail.getFq_num() - 1)));
                detail.setFirst_each_money(getMoney(first_each.divide(index).toString()));
            } else {
                //商家承担手续费
                BigDecimal moneyBD = cent.subtract(pos_fee);
                detail.setBusiness_real_money(getMoney((moneyBD.divide(index)).subtract(new BigDecimal(dealFee)).toString()));
                detail.setCustomer_real_money(getMoney(cent.divide(index).toString()));
                BigDecimal total_moneyBD = cent;
                detail.setFq_fee(getMoney(pos_fee.divide(index).toString()));
                detail.setCost_fee(getMoney(ali_fq_fee.divide(index).toString()));
                BigDecimal royalty_fee = pos_fee.subtract(ali_fq_fee);
                detail.setFq_profit_money(getMoney(royalty_fee.divide(index).toString()));
                detail.setOrder_real_money(getMoney(total_moneyBD.divide(index).toString()));
                BigDecimal fq_eachDB = cent.divide(new BigDecimal(detail.getFq_num()), 4, RoundingMode.HALF_EVEN);
                detail.setFq_each_money(getMoney(fq_eachDB.divide(index).toString()));
                BigDecimal first_each = cent.subtract(fq_eachDB.multiply(new BigDecimal(detail.getFq_num() - 1)));
                detail.setFirst_each_money(getMoney(first_each.divide(index).toString()));
            }
            return detail;
        } catch (Exception e) {
            return detail;
        }
    }

    /**
     * 计算金额（预授权，解冻金额）
     *
     * @param money  交易金额
     * @param fq_num 分期期数
     * @param rate   分期成本手续费率
     */
    public static CalculateDetail calculate(String money, Integer fq_num, String rate) {
        CalculateDetail detail = new CalculateDetail();
        detail.setMoney(formatMoney(money));
        detail.setFqNum(fq_num);
        detail.setRate(rate);
        BigDecimal interest = new BigDecimal(detail.getMoney()).multiply(new BigDecimal(detail.getRate())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        detail.setInterest(interest.toString());
        detail.setTotalMoney(new BigDecimal(detail.getMoney()).add(interest).toString());
        if (fq_num > 0) {
            BigDecimal each = new BigDecimal(detail.getMoney()).divide(new BigDecimal(fq_num), 0, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal special = new BigDecimal(detail.getMoney()).subtract(each.multiply(new BigDecimal(fq_num).subtract(new BigDecimal(1))));
            detail.setEach(each.toString());
            detail.setSpecial(special.toString());
        }
        return detail;
    }

    /**
     * 计算金额（预授权，还款金额）
     *
     * @param money  交易金额
     * @param fq_num 分期期数
     * @param rate   分期成本手续费率
     */
    public static CalculateDetail calculateRate(String money, Integer fq_num, String rate) {
        CalculateDetail detail = new CalculateDetail();
        detail.setMoney(formatMoney(money));
        detail.setFqNum(fq_num);
        detail.setRate(rate);
        BigDecimal interest = new BigDecimal(detail.getMoney()).multiply(new BigDecimal(detail.getRate())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        detail.setInterest(interest.toString());
        detail.setTotalMoney(new BigDecimal(detail.getMoney()).add(interest).toString());
        if (fq_num > 0) {
            BigDecimal each = new BigDecimal(detail.getTotalMoney()).divide(new BigDecimal(fq_num), 0, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal special = new BigDecimal(detail.getTotalMoney()).subtract(each.multiply(new BigDecimal(fq_num).subtract(new BigDecimal(1))));
            detail.setEach(each.toString());
            detail.setSpecial(special.toString());
        }
        return detail;
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
        BigDecimal total = new BigDecimal(money1).add(new BigDecimal(money2));
        return total.toString();
    }

    /**
     * 金额相加
     *
     * @param money1 金额1
     * @param money2 金额2
     */
    public static String devide(String money1, String money2) {
        BigDecimal total = new BigDecimal(money1).divide(new BigDecimal(money2));
        return total.toString();
    }

    /**
     * 金额取整
     *
     * @param money
     * @param type  0-不取整 1-分 2-角 3-元
     */
    public static String moneyRounding(String money, Integer type) {
        String value = null;
        if (type == 0) {
            return value;
        }
        Integer index = null;
        if (type == 1) {
            index = 1000;
        } else if (type == 2) {
            index = 100;
        } else {
            index = 10;
        }
        Double s = Double.valueOf(money) * index;
        Double j = s % 100;
        return new BigDecimal(j).divide(new BigDecimal(index)).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
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

    /**
     * 获取金额，若金额为整数，则显示整数，若金额为小数，则保留两位小数
     *
     * @param rate
     * @return
     */
    public static String formatRate(String rate) {
        if (rate == null) {
            return "0.0000";
        }
        BigDecimal decimal = new BigDecimal("0");
        try {
            decimal.add(new BigDecimal(rate));
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0000";
        }
        return decimal.setScale(4, BigDecimal.ROUND_HALF_EVEN).toString();
    }


    /**
     * 获取金额，若金额为整数，则显示整数，若金额为小数，则保留两位小数
     *
     * @param total_money
     * @return
     */
    public static String getMoney(String total_money) {
        if (total_money == null) {
            return "0.00";
        }
        //判断total_money中是否包含 "."
        int ind = total_money.indexOf(".");
        //ind如果不等于-1，代表存在"."
        if (ind != -1) {
            String[] money = total_money.split("\\.");
            Double f = Double.valueOf(money[1]);
            if (f >= 0) {
                BigDecimal b = new BigDecimal(total_money);
                //保留2位小数
                DecimalFormat df = new DecimalFormat("0.00");
                String scale_3 = b.setScale(3, BigDecimal.ROUND_HALF_EVEN).toString();
                String m = df.format(new BigDecimal(scale_3).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
                return m;
            } else {
                return money[0];
            }
        } else {
            total_money = total_money + ".00";
        }
        return total_money;
    }

    /**
     * 比较金额 是否大于 0
     *
     * @param number 金额1
     */
    public static boolean zeroMoney(Object number) {
        if (number == null) {
            return false;
        }
        try {
            Integer result = new BigDecimal(String.valueOf(number)).compareTo(new BigDecimal(0));
            return result == 1;
        } catch (Exception e) {
            return false;
        }
    }

}

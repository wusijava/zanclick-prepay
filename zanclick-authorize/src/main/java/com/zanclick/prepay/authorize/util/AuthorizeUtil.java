package com.zanclick.prepay.authorize.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 *
 * @author duchong
 * @date 2019-7-9 10:44:42
 */
public class AuthorizeUtil {


    /**
     * 还款日期
     */
    static SimpleDateFormat dateOfRepayment = new SimpleDateFormat("yyyy-MM");


    /**
     * 获取格式化还款日期
     *
     * @return
     */
    public static String getSettleDate() {
        return dateOfRepayment.format(new Date());
    }

    /**
     * 获取格式化还款日期
     *
     * @param settleDate
     * @return
     */
    public static String getDateOfRepayment(Date settleDate) {
        if (settleDate == null){
            settleDate = new Date();
        }
        return dateOfRepayment.format(settleDate);
    }
}

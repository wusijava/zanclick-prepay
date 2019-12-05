//package com.zanclick.prepay.web;
//
//import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
//import com.zanclick.prepay.authorize.service.RedPacketConfigurationService;
//import com.zanclick.prepay.authorize.util.MoneyUtil;
//import com.zanclick.prepay.common.utils.PassWordUtil;
//import com.zanclick.prepay.common.utils.StringUtils;
//import com.zanclick.prepay.order.entity.Area;
//import com.zanclick.prepay.order.service.AreaService;
//import com.zanclick.prepay.setmeal.entity.SetMeal;
//import com.zanclick.prepay.setmeal.service.SetMealService;
//import com.zanclick.prepay.user.entity.User;
//import com.zanclick.prepay.user.service.UserService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author lvlu
// * @date 2019-07-06 14:32
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = WebApplication.class)
//public class ZftTest {
//
//    @Autowired
//    private SetMealService setMealService;
//    @Autowired
//    private RedPacketConfigurationService redPacketConfigurationService;
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void sss() {
//        String amount = redPacketConfigurationService.queryRedPacketAmount("13027129244","200","200","1000.00",24);
//        System.err.println(amount);
//    }
//
//
//    @Test
//    public void s() {
//        String s = "";
//        String[] setMeals = s.split("\n");
//        List<SetMeal> mealList = new ArrayList<>();
//        StringBuffer insert = new StringBuffer();
//        StringBuffer update = new StringBuffer();
//        for (String sm : setMeals) {
//            String[] strings = sm.split("\\|");
//            SetMeal meal = new SetMeal();
//            meal.setPackageNo(strings[0]);
//            meal.setTradeMark(strings[1]);
//            meal.setTitle(strings[2]);
//            meal.setTotalAmount(MoneyUtil.formatMoney(strings[5]));
//            meal.setNum(Integer.valueOf(strings[4]));
//            meal.setAmount(MoneyUtil.divide(meal.getTotalAmount(), meal.getNum().toString()));
//            meal.setAppId("201910091625131208151");
//            meal.setRedPackAmount(getRedAmount(meal.getTotalAmount()));
//            meal.setState(1);
//            if (meal.getNum() == 24) {
//                meal.setRedPackState(1);
//            } else {
//                meal.setRedPackState(0);
//            }
//            if (meal.getNum().equals(12)) {
//                meal.setSettleAmount(MoneyUtil.formatMoney(jisuans(meal.getTotalAmount(), "1.1")));
//            } else if (meal.getNum().equals(24)) {
//                meal.setSettleAmount(MoneyUtil.formatMoney(jisuan(meal.getTotalAmount(), "1.2")));
//            }
//            SetMeal setMeal = setMealService.queryByPackageNo(meal.getPackageNo());
//            if (setMeal != null) {
//                System.err.println(setMeal.getPackageNo());
//                insert.append(meal.getId()).append(",");
//            } else {
//                setMealService.insert(meal);
//                insert.append(meal.getId()).append(",");
//            }
//            mealList.add(meal);
//        }
//        System.err.println(insert.toString());
//        System.err.println(update.toString());
//    }
//
//
//    private String getRedAmount(String amount) {
//        int i = new BigDecimal(amount).compareTo(new BigDecimal(800));
//        int j = new BigDecimal(amount).compareTo(new BigDecimal(1000));
//        if (i == -1) {
//            return "15.00";
//        }
//        if ((i == 1 || i == 0) && j == -1) {
//            return "25.00";
//        }
//        if (j == 0 || j == 1) {
//            return "30.00";
//        }
//        return null;
//    }
//
//
//    private String jisuan(String money, String fee) {
//        return MoneyUtil.divide(money, fee);
//    }
//
//    private String jisuans(String money, String fee) {
//        Double my = Double.valueOf(MoneyUtil.divide(money, fee));
//        Double yushu = my % 5;
//        if (yushu > 2.5) {
//            return MoneyUtil.formatMoney(String.valueOf(my - yushu + 5d));
//        } else {
//            return MoneyUtil.formatMoney(String.valueOf(my - yushu));
//        }
//    }
//}

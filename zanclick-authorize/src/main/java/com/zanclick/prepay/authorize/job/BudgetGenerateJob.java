package com.zanclick.prepay.authorize.job;

import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.query.AuthorizeOrderQuery;
import com.zanclick.prepay.authorize.service.AuthorizeBudgetService;
import com.zanclick.prepay.authorize.service.AuthorizeOrderService;
import com.zanclick.prepay.common.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author lvlu
 * @date 2019/12/10 18:48
 */
@Component
public class BudgetGenerateJob {

    @Value("${authorize.budget.pay-date}")
    private Integer payDate;

    @Value("${authorize.budget.late-days}")
    private Integer lateDays;

    @Autowired
    private AuthorizeBudgetService authorizeBudgetService;

    @Autowired
    private AuthorizeOrderService authorizeOrderService;


    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduled() {
        AuthorizeOrderQuery query = new AuthorizeOrderQuery();
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(lateDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        query.setAuthNoStart(localDate.format(formatter));
        Integer limit = 1000;
        query.setLimit(limit);
        List<AuthorizeOrder> list;
        Long nextIndex = null;
        for (; ; ) {
            query.setNextIndex(nextIndex);
            list = authorizeOrderService.queryList(query);
            generateBudget(list);
            if (DataUtil.isEmpty(list) || list.size() < limit) {
                break;
            }
        }
    }

    private void generateBudget(List<AuthorizeOrder> orders) {
        orders.forEach(order -> authorizeBudgetService.generateBudget(order, payDate));
    }

}

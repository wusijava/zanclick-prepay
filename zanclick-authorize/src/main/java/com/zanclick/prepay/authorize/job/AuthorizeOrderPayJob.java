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
 * @date 2019/12/10 19:13
 */
@Component
public class AuthorizeOrderPayJob {

    @Value("${authorize.budget.pay-date}")
    private Integer payDate;

    @Value("${authorize.budget.late-days}")
    private Integer lateDays;

    @Autowired
    private AuthorizeBudgetService authorizeBudgetService;

    @Scheduled(cron = "0 0 6 ${authorize.budget.pay-date} * ?")
    public void scheduled() {
        authorizeBudgetService.createAuthorizePayOrderFromBudget();
    }

}

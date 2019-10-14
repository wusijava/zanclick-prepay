package com.zanclick.prepay.setmeal.scheduled;

import com.zanclick.prepay.app.entity.AppSftpConfig;
import com.zanclick.prepay.app.service.AppSftpConfigService;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.service.SetMealService;
import com.zanclick.prepay.setmeal.util.SetMealManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SyncSetMealJob {

    @Autowired
    private AppSftpConfigService appSftpConfigService;

    @Autowired
    private SetMealService setMealService;

    @Scheduled(cron = "0 0 4 * * * *")
    public void work() {
        log.info("开始同步套餐信息--------");
        AppSftpConfig query = new AppSftpConfig();
        query.setType(1);
        query.setState(1);
        List<AppSftpConfig> appInfos = appSftpConfigService.queryList(query);
        if (DataUtil.isNotEmpty(appInfos)) {
            return;
        }
        appInfos.forEach(appInfo -> {
            SetMealManager setMealManager = new SetMealManager(appInfo.getAppId(), appInfo.getHost(), appInfo.getPort(),
                    appInfo.getUsername(), appInfo.getPassword(), appInfo.getRemoteDir(), appInfo.getLocalDir());
            List<SetMeal> setMeals = setMealManager.getNewestSetMeals();
            setMealService.unshelveSetMealByAppId(appInfo.getAppId());
            if (DataUtil.isNotEmpty(setMeals)) {
                setMeals.forEach(setMeal -> {
                    SetMeal setMealQuery = new SetMeal();
                    setMealQuery.setAppId(appInfo.getAppId());
                    setMealQuery.setPackageNo(setMeal.getPackageNo());
                    SetMeal oldSetMeal = setMealService.queryOne(setMealQuery);
                    if (DataUtil.isNotEmpty(oldSetMeal)) {
                        setMeal.setAppId(appInfo.getAppId());
                        setMeal.setId(oldSetMeal.getId());
                        setMeal.setState(1);
                        setMealService.updateById(setMeal);
                    } else {
                        setMeal.setState(1);
                        setMealService.insert(setMeal);
                    }
                });
            }
        });
    }

}

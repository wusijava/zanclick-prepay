package com.zanclick.prepay.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.alipay.api.response.MybankCreditSupplychainTradeBillrepaybudgetQueryResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.entity.SupplyChainTrade;
import com.zanclick.prepay.authorize.pay.AuthorizePayService;
import com.zanclick.prepay.authorize.service.*;
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.*;
import com.zanclick.prepay.common.config.JmsMessaging;
import com.zanclick.prepay.common.config.SendMessage;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import com.zanclick.prepay.setmeal.service.SetMealService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lvlu
 * @date 2019-07-06 14:32
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class ZftTest {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;
    @Autowired
    private AuthorizePayService authorizePayService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private MyBankSupplyChainService myBankSupplyChainService;
    @Autowired
    private AuthorizeOrderService authorizeOrderService;
    @Autowired
    private SetMealService setMealService;

    @Test
    public void sssss(){
        PayOrder order = payOrderService.queryById(14L);
        AuthorizeOrder authorizeOrder = authorizeOrderService.queryByRequestNo(order.getRequestNo());
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        MybankCreditSupplychainTradeBillrepaybudgetQueryResponse response = SupplyChainUtils.tradeBillRepayBudgetQuery(authorizeOrder.getAuthNo(),configuration);

        System.err.println(JSONObject.toJSONString(
                response
        ));
    }

    @Test
    public void ssss(){
        List<PayOrder> payOrderList = payOrderService.queryList(new PayOrder());
        for (PayOrder order:payOrderList){
            AuthorizeMerchant merchant = queryOne(order.getMerchantNo());
            if (merchant == null){
                payOrderService.deleteById(order.getId());
            }
            order.setWayId(merchant.getWayId());
            order.setStoreName(merchant.getStoreName());
            if (order.getState().equals(0) || order.getState().equals(-1)){
                order.setDealState(PayOrder.DealState.notice_wait.getCode());
            }
        }

    }

    private AuthorizeMerchant queryOne(String merchantNo){
        AuthorizeMerchant merchant = new AuthorizeMerchant();
        merchant.setState(1);
        merchant.setMerchantNo(merchantNo);
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(merchant);
        if (DataUtil.isNotEmpty(merchantList)){
            return merchantList.get(0);
        }
        return null;
    }


    @Test
    public void sss(){
        List<RegisterMerchant> merchantList = ExcelUtil.readExcel("E:\\excel\\2019110902.xls","Sheet1");
        for (RegisterMerchant merchant:merchantList){
            createAuthorizeMerchant(merchant);
        }
    }

    @Test
    public void ss(){
        AuthorizeMerchant query = new AuthorizeMerchant();
        query.setState(0);
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        for (AuthorizeMerchant merchant:merchantList){
            try {
                createSupplier(merchant);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Test
    public void s(){
        String s = "7d169f11-a2a7-3fee-a429-b9b5512db81c|ZDJXRW881230FK|（201908新入网自带机）承诺使用88元及以上4G套餐12个月每月减免30元话费（非插卡类）|88元/月|12|360\n" +
                "cfe78787-1705-3d2b-a704-9f6597a09610|ZDJXRW881230K|（201908新入网自带机）承诺使用88元及以上4G套餐12个月每月减免30元话费（插卡类）|88元/月|12|360\n" +
                "ad603c97-1b29-3224-8fa0-5af598a41ee5|ZDJXRW882430FK|（201908新入网自带机）承诺使用88元及以上4G套餐24个月每月减免30元话费（非插卡类）|88元/月|24|720\n" +
                "0f8ecac0-b5fe-3305-aeff-a61b22f1c4e3|ZDJXRW882430K|（201908新入网自带机）承诺使用88元及以上4G套餐24个月每月减免30元话费（插卡类）|88元/月|24|720\n" +
                "1dd3216e-eb70-3998-94af-6209a16b0ce9|ZDJXRW1081230FK|（201908新入网自带机）承诺使用108元及以上4G套餐12个月每月减免30元话费（非插卡类）|108元/月|12|360\n" +
                "84660066-a3c5-3264-ab51-4ab2590251d2|ZDJXRW1081230K|（201908新入网自带机）承诺使用108元及以上4G套餐12个月每月减免30元话费（插卡类）|108元/月|12|360\n" +
                "e1ac0fe5-7077-3179-9b4e-5b4b08e64c0b|ZDJXRW1082430FK|（201908新入网自带机）承诺使用108元及以上4G套餐24个月每月减免30元话费（非插卡类）|108元/月|24|720\n" +
                "5324c739-17b2-3d73-b4a1-03cff92440fd|ZDJXRW1082430K|（201908新入网自带机）承诺使用108元及以上4G套餐24个月每月减免30元话费（插卡类）|108元/月|24|720\n" +
                "a89ac832-e468-3b31-8c82-78305321bb04|ZDJXRW1381250FK|（201908新入网自带机）承诺使用138元及以上4G套餐12个月每月减免50元话费（非插卡类）|138元/月|12|600\n" +
                "70875b1f-d363-3389-a118-aab305db2c36|ZDJXRW1381250K|（201908新入网自带机）承诺使用138元及以上4G套餐12个月每月减免50元话费（插卡类）|138元/月|12|600\n" +
                "ffb057a2-6fa8-3cd5-a39b-40ab9b2ad9b5|ZDJXRW1382450FK|（201908新入网自带机）承诺使用138元及以上4G套餐24个月每月减免50元话费（非插卡类）|138元/月|24|1200\n" +
                "849d1991-1297-3d41-8c6b-34bfe8d9786f|ZDJXRW1382450K|（201908新入网自带机）承诺使用138元及以上4G套餐24个月每月减免50元话费（插卡类）|138元/月|24|1200\n" +
                "690ffdd3-01e7-3a58-90d7-2801ec92cdd7|ZDJXRW1881270FK|（201908新入网自带机）承诺使用188元及以上4G套餐12个月每月减免70元话费（非插卡类）|188元/月|12|840\n" +
                "5b4a5174-cc58-3fde-8705-efdd4482adbc|ZDJXRW1881270K|（201908新入网自带机）承诺使用188元及以上4G套餐12个月每月减免70元话费（插卡类）|188元/月|12|840\n" +
                "bc9d3ea2-cbb2-36ee-99cc-699edbbc614e|ZDJXRW1882470FK|（201908新入网自带机）承诺使用188元及以上4G套餐24个月每月减免70元话费（非插卡类）|188元/月|24|1680\n" +
                "6782bfaa-0ad3-38be-8a18-0aa14d4e58f2|ZDJXRW1882470K|（201908新入网自带机）承诺使用188元及以上4G套餐24个月每月减免70元话费（插卡类）|188元/月|24|1680\n" +
                "2240358f-f65e-3c60-82a7-01af17553f0d|ZDJCL881225FK|（201910存量自带机）承诺升档至88元及以上4G套餐12个月每月减免25元话费（88元及以下_非插卡类）|88元/月|12|300\n" +
                "a1f3c118-979a-3ca7-a868-a31187cd05b7|ZDJCL881225K|（201910存量自带机）承诺升档至88元及以上4G套餐12个月每月减免25元话费（88元及以下_插卡类）|88元/月|12|300\n" +
                "e021151d-1b6e-32cb-a24c-7d391ab8524f|ZDJCL882425FK|（201910存量自带机）承诺升档至88元及以上4G套餐24个月每月减免25元话费（88元及以下_非插卡类）|88元/月|24|600\n" +
                "c8d41207-b0a5-3304-ab9f-5c729a583679|ZDJCL882425K|（201910存量自带机）承诺升档至88元及以上4G套餐24个月每月减免25元话费（88元及以下_插卡类）|88元/月|24|600\n" +
                "eda60cb5-d842-37b2-a81b-9cb105a198f9|ZDJCL1081230FK|（201910存量自带机）承诺升档至108元及以上4G套餐12个月每月减免30元话费（88元及以下_非插卡类）|108元/月|12|360\n" +
                "255a414f-57ec-3057-81a9-537aa67bfadb|ZDJCL1081230K|（201910存量自带机）承诺升档至108元及以上4G套餐12个月每月减免30元话费（88元及以下_插卡类）|108元/月|12|360\n" +
                "c52ee801-bf59-355e-9ebe-7f953ccc24b1|ZDJCL1082430FK|（201910存量自带机）承诺升档至108元及以上4G套餐24个月每月减免30元话费（88元及以下_非插卡类）|108元/月|24|720\n" +
                "e9ef68c2-5bfe-3272-b7a4-49cbf6ae073f|ZDJCL1082430K|（201910存量自带机）承诺升档至108元及以上4G套餐24个月每月减免30元话费（88元及以下_插卡类）|108元/月|24|720\n" +
                "16a42fbe-7118-37d3-a9ef-da7d897ed74b|ZDJCL1381240FK|（201908存量自带机）承诺升档至138元及以上4G套餐12个月每月减免40元话费（88元及以下_非插卡类）|138元/月|12|480\n" +
                "cbb3caf6-f1ae-331e-b9ac-182c9b5b2d39|ZDJCL1381240K|（201908存量自带机）承诺升档至138元及以上4G套餐12个月每月减免40元话费（88元及以下_插卡类）|138元/月|12|480\n" +
                "ed4d7405-7445-3c24-93d6-f3fbeb925fc7|ZDJCL1382440FK|（201908存量自带机）承诺升档至138元及以上4G套餐24个月每月减免40元话费（88元及以下_非插卡类）|138元/月|24|960\n" +
                "aec76fda-cb3b-31f9-8f54-5d1977440f80|ZDJCL1382440K|（201908存量自带机）承诺升档至138元及以上4G套餐24个月每月减免40元话费（88元及以下_插卡类）|138元/月|24|960\n" +
                "6ceb5287-30a2-3113-9321-e5a913942906|ZDJCL1881270FK|（201908存量自带机）承诺升档至188元及以上4G套餐12个月每月减免70元话费（88元及以下_非插卡类）|188元/月|12|840\n" +
                "38ecf78e-c49b-3820-9000-9a15c06a496c|ZDJCL1881270K|（201908存量自带机）承诺升档至188元及以上4G套餐12个月每月减免70元话费（88元及以下_插卡类）|188元/月|12|840\n" +
                "391acd1e-b39b-34d1-b1e5-4b3151fb3bf1|ZDJCL1881240FK|（201908存量自带机）承诺升档至188元及以上4G套餐12个月每月减免40元话费（138元升档_非插卡类）|188元/月|12|480\n" +
                "5fe1343d-604a-3862-b3c0-3adf66dfeb04|ZDJCL1881240K|（201908存量自带机）承诺升档至188元及以上4G套餐12个月每月减免40元话费（138元升档_插卡类）|188元/月|12|480\n" +
                "5f3928fd-27cb-33a8-873f-5eb9942641db|ZDJCL1882470FK|（201908存量自带机）承诺升档至188元及以上4G套餐24个月每月减免70元话费（88元及以下_非插卡类）|188元/月|24|1680\n" +
                "3cdce8ec-f930-3342-bbb1-9ab2a1b704a3|ZDJCL1882470K|（201908存量自带机）承诺升档至188元及以上4G套餐24个月每月减免70元话费（88元及以下_插卡类）|188元/月|24|1680\n" +
                "b633ea6c-9de1-32df-a431-a91fe2d81db7|ZDJCL1882440FK|（201908存量自带机）承诺升档至188元及以上4G套餐24个月每月减免40元话费（138元升档_非插卡类）|188元/月|24|960\n" +
                "658560fc-c97f-3c21-a7cf-8d021f4c2c0a|ZDJCL1882440K|（201908存量自带机）承诺升档至188元及以上4G套餐24个月每月减免40元话费（138元升档_插卡类）|188元/月|24|960\n" +
                "f04f8d37-0a69-3d03-8ac9-b75329adcb4b|ZDJCL2381270FK|（201908存量自带机）承诺升档至238元及以上4G套餐12个月每月减免70元话费（138元及以下_非插卡类）|238元/月|12|840\n" +
                "f97817bb-bc0f-33b4-883d-119dee69eeaf|ZDJCL2381270K|（201908存量自带机）承诺升档至238元及以上4G套餐12个月每月减免70元话费（138元及以下_插卡类）|238元/月|12|840\n" +
                "b6b40001-ba39-3d30-a414-d84e8c91a875|ZDJCL2381240FK|（201908存量自带机）承诺升档至238元及以上4G套餐12个月每月减免40元话费（188元升档_非插卡类）|238元/月|12|480\n" +
                "b56551b0-f4ba-3c2a-a343-c7ca79a6261f|ZDJCL2381240K|（201908存量自带机）承诺升档至238元及以上4G套餐12个月每月减免40元话费（188元升档_插卡类）|238元/月|12|480\n" +
                "36fce4dd-ca6e-398c-b062-0865638b0464|ZDJCL2382470FK|（201908存量自带机）承诺升档至238元及以上4G套餐24个月每月减免70元话费（138元及以下_非插卡类）|238元/月|24|1680\n" +
                "7dc77566-70d6-3549-a0c3-d9722726e369|ZDJCL2382470K|（201908存量自带机）承诺升档至238元及以上4G套餐24个月每月减免70元话费（138元及以下_插卡类）|238元/月|24|1680\n" +
                "c19c73f0-0203-3b84-8e9c-33b5c93b1be6|ZDJCL2382440FK|（201908存量自带机）承诺升档至238元及以上4G套餐24个月每月减免40元话费（188元升档_非插卡类）|238元/月|24|960\n" +
                "1a5a9aa8-aaa4-3d50-90bc-5d3c11475a97|ZDJCL2382440K|（201908存量自带机）承诺升档至238元及以上4G套餐24个月每月减免40元话费（188元升档_插卡类）|238元/月|24|960\n" +
                "acce506f-7249-3154-b68c-83922ca01842|ZDJCL2881270FK|（201908存量自带机）承诺升档至288元及以上4G套餐12个月每月减免70元话费（188元及以下_非插卡类）|288元/月|12|840\n" +
                "2a485fd5-2f69-39a4-91bc-a01465bda85f|ZDJCL2881270K|（201908存量自带机）承诺升档至288元及以上4G套餐12个月每月减免70元话费（188元及以下_插卡类）|288元/月|12|840\n" +
                "0f67f9b2-0d80-3664-8f3c-10ed7e17d109|ZDJCL2881240FK|（201908存量自带机）承诺升档至288元及以上4G套餐12个月每月减免40元话费（238元升档_非插卡类）|288元/月|12|480\n" +
                "3eb7c99a-2b75-37a6-8538-168584027e4d|ZDJCL2881240K|（201908存量自带机）承诺升档至288元及以上4G套餐12个月每月减免40元话费（238元升档_插卡类）|288元/月|12|480\n" +
                "51523e37-3eb4-393c-ba07-033002c4b229|ZDJCL2882470FK|（201908存量自带机）承诺升档至288元及以上4G套餐24个月每月减免70元话费（188元及以下_非插卡类）|288元/月|24|1680\n" +
                "94cc099d-45d9-3b6f-b0aa-64b4b19a294e|ZDJCL2882470K|（201908存量自带机）承诺升档至288元及以上4G套餐24个月每月减免70元话费（188元及以下_插卡类）|288元/月|24|1680\n" +
                "a78d5fa7-2264-3127-b7f7-e7dabbe1593b|ZDJCL2882440FK|（201908存量自带机）承诺升档至288元及以上4G套餐24个月每月减免40元话费（238元升档_非插卡类）|288元/月|24|960\n" +
                "e24f5451-5e0d-32b8-b8d6-6c46dab1845b|ZDJCL2882440K|（201908存量自带机）承诺升档至288元及以上4G套餐24个月每月减免40元话费（238元升档_插卡类）|288元/月|24|960\n" +
                "6ae80ca9-eeee-3a99-8d19-c89b73e7d29b|ZDJCL1218855K|（201910存量自带机）承诺升档至188元及以上4G套餐12个月每月减免55元话费（88元及以下升档_插卡类）|188元/月|12|660\n" +
                "70260769-a24a-39f0-b1b9-a6b95235ca13|ZDJCL1218855FK|（201910存量自带机）承诺升档至188元及以上4G套餐12个月每月减免55元话费（88元及以下升档_非插卡类）|188元/月|12|660\n" +
                "9681bd34-d793-3a6b-934f-3c725724445f|ZDJCL2418855K|（201910存量自带机）承诺升档至188元及以上4G套餐24个月每月减免55元话费（88元及以下升档_插卡类）|188元/月|24|1320\n" +
                "64c6aa88-ecb2-3013-b37e-ef7cce19bb7b|ZDJCL2418855FK|（201910存量自带机）承诺升档至188元及以上4G套餐24个月每月减免55元话费（88元及以下升档_非插卡类）|188元/月|24|1320\n" +
                "4498b72d-6e75-39e9-a143-acdf1c005ab2|ZDJCL1223850K|（201910存量自带机）承诺升档至238元及以上4G套餐12个月每月减免50元话费（188元及以下升档_插卡类）|238元/月|12|600\n" +
                "2d8fd0b4-5fcf-3ba8-9354-e2e47c4f066a|ZDJCL1223850FK|（201910存量自带机）承诺升档至238元及以上4G套餐12个月每月减免50元话费（188元及以下升档_非插卡类）|238元/月|12|600\n" +
                "95d61bdf-7c93-38fa-a432-551254ba0b44|ZDJCL1223850K|（201910存量自带机）承诺升档至238元及以上4G套餐12个月每月减免50元话费（188元及以下升档_插卡类）|238元/月|24|1200\n" +
                "79bf6c88-88bc-31ed-8083-a396b6811845|ZDJCL1223850FK|（201910存量自带机）承诺升档至238元及以上4G套餐12个月每月减免50元话费（188元及以下升档_非插卡类）|238元/月|24|1200\n" +
                "6a793a36-588e-3678-b143-17047f689d85|ZDJCL1228860K|（201910存量自带机）承诺升档至288元及以上4G套餐12个月每月减免60元话费（238元升档_插卡类）|288元/月|12|720\n" +
                "f333d935-e2b6-3240-98b5-eb1a4d3f75e3|ZDJCL1228860FK|（201910存量自带机）承诺升档至288元及以上4G套餐12个月每月减免60元话费（238元升档_非插卡类）|288元/月|12|720\n" +
                "b4197ff8-7014-385e-ab2d-dbc61d7a9c39|ZDJCL1228880K|（201910存量自带机）承诺升档至288元及以上4G套餐12个月每月减免80元话费（188元及以下升档_插卡类）|288元/月|12|960\n" +
                "b35243af-a571-38d2-a6e5-981684e17390|ZDJCL1228880FK|（201910存量自带机）承诺升档至288元及以上4G套餐12个月每月减免80元话费（188元及以下升档_非插卡类）|288元/月|12|960\n" +
                "9aa01eb6-be9f-35af-a59c-1bebd8138326|ZDJCL2428860K|（201910存量自带机）承诺升档至288元及以上4G套餐24个月每月减免60元话费（238元升档_插卡类）|288元/月|24|1440\n" +
                "deaab59d-5e7b-3edd-bfa2-856521c0196f|ZDJCL2428860FK|（201910存量自带机）承诺升档至288元及以上4G套餐24个月每月减免60元话费（238元升档_非插卡类）|288元/月|24|1440\n" +
                "bf185f7d-1b0f-3f02-98ac-2b18a8c0171b|ZDJCL2428880K|（201910存量自带机）承诺升档至288元及以上4G套餐24个月每月减免80元话费（188元及以下升档_插卡类）|288元/月|24|1920\n" +
                "22fd73cd-5990-3177-a31f-354e14eb53e0|ZDJCL2428880FK|（201910存量自带机）承诺升档至288元及以上4G套餐24个月每月减免80元话费（188元及以下升档_非插卡类）|288元/月|24|1920\n" +
                "e41c4757-d6d6-31e3-90f3-980057ae6629|ZDJCLX1381230K|（201910存量自带机）承诺使用138元及以上4G套餐12个月每月减免30元话费（插卡类）|138元/月|12|360\n" +
                "5bc25435-fab9-3b3c-ae17-a29246c66011|ZDJCLX1381230FK|（201910存量自带机）承诺使用138元及以上4G套餐12个月每月减免30元话费（非插卡类）|138元/月|12|360\n" +
                "406c3619-3526-3cb4-80da-db3b25c98615|ZDJCLX1382430K|（201910存量自带机）承诺使用138元及以上4G套餐24个月每月减免30元话费（插卡类）|138元/月|24|720\n" +
                "5970f74d-4a42-3915-a6e8-d581539584a7|ZDJCLX1382430FK|（201910存量自带机）承诺使用138元及以上4G套餐24个月每月减免30元话费（非插卡类）|138元/月|24|720\n" +
                "8d76a2b8-c44e-3d49-bf9a-07a22003035f|ZDJCLX1881240K|（201910存量自带机）承诺使用188元及以上4G套餐12个月每月减免40元话费（插卡类）|188元/月|12|480\n" +
                "e264f49a-421c-3f0c-8826-75643ae2d565|ZDJCLX1881240FK|（201910存量自带机）承诺使用188元及以上4G套餐12个月每月减免40元话费（非插卡类）|188元/月|12|480\n" +
                "72282c75-da4f-3885-81cd-28bfe04f4f17|ZDJCLX1882440K|（201910存量自带机）承诺使用188元及以上4G套餐24个月每月减免40元话费（插卡类）|188元/月|24|960\n" +
                "d06b7414-90d2-36d7-a02f-377cbdbe2c02|ZDJCLX1882440FK|（201910存量自带机）承诺使用188元及以上4G套餐24个月每月减免40元话费（非插卡类）|188元/月|24|960\n" +
                "3dba86d0-c1eb-3292-86d4-2030a266320b|ZDJCLX2381240K|（201910存量自带机）承诺使用238元及以上4G套餐12个月每月减免40元话费（插卡类）|238元/月|12|480\n" +
                "9bf5b254-46ec-322d-80dd-8e688b958abb|ZDJCLX2381240FK|（201910存量自带机）承诺使用238元及以上4G套餐12个月每月减免40元话费（非插卡类）|238元/月|12|480\n" +
                "184b8ec5-dcc6-3a63-a522-6076d5bb3307|ZDJCLX2382440K|（201910存量自带机）承诺使用238元及以上4G套餐24个月每月减免40元话费（插卡类）|238元/月|24|960\n" +
                "27241389-b954-37ec-b7ed-f1930ccdf396|ZDJCLX2382440FK|（201910存量自带机）承诺使用238元及以上4G套餐24个月每月减免40元话费（非插卡类）|238元/月|24|960\n" +
                "acf02478-9d2c-3e7f-a4ba-708abdce450d|ZDJCLX2881250K|（201910存量自带机）承诺使用288元及以上4G套餐12个月每月减免50元话费（插卡类）|288元/月|12|600\n" +
                "f5df7ecf-5399-3fc8-b5df-5b5d89aa613b|ZDJCLX2881250FK|（201910存量自带机）承诺使用288元及以上4G套餐12个月每月减免50元话费（非插卡类）|288元/月|12|600\n" +
                "97b09ada-f60f-34ca-bc6e-6f0a0283a0b9|ZDJCLX2882450K|（201910存量自带机）承诺使用288元及以上4G套餐24个月每月减免50元话费（插卡类）|288元/月|24|1200\n" +
                "4f1a71ff-7fdf-3972-946c-820e5d6e24d6|ZDJCLX2882450FK|（201910存量自带机）承诺使用288元及以上4G套餐24个月每月减免50元话费（非插卡类）|288元/月|24|1200\n" +
                "bd9d616c-54ba-334f-b4b2-220986021bfb|ZDJCLX3881270K|（201910存量自带机）承诺使用388元及以上4G套餐12个月每月减免70元话费（插卡类）|388元/月|12|840\n" +
                "6522540e-fa70-3a5a-94d6-161242d772f3|ZDJCLX3881270FK|（201910存量自带机）承诺使用388元及以上4G套餐12个月每月减免70元话费（非插卡类）|388元/月|12|840\n" +
                "bf9370eb-907a-37a9-9f77-732c11868b8a|ZDJCLX3882470K|（201910存量自带机）承诺使用388元及以上4G套餐24个月每月减免70元话费（插卡类）|388元/月|24|1680\n" +
                "f688500b-e2e7-34af-98a6-6cf101325a36|ZDJCLX3882470FK|（201910存量自带机）承诺使用388元及以上4G套餐24个月每月减免70元话费（非插卡类）|388元/月|24|1680\n" +
                "e9791d71-baf2-36a8-abf6-c828f53ae405|ZDJCLX58812100K|（201910存量自带机）承诺使用588元及以上4G套餐12个月每月减免100元话费（插卡类）|588元/月|12|1200\n" +
                "188a564f-c688-3b43-b64a-743353704853|ZDJCLX58812100FK|（201910存量自带机）承诺使用588元及以上4G套餐12个月每月减免100元话费（非插卡类）|588元/月|12|1200\n" +
                "54935392-7574-3e51-b501-1d67f2226e4e|ZDJCLX58824100K|（201910存量自带机）承诺使用588元及以上4G套餐24个月每月减免100元话费（插卡类）|588元/月|24|2400\n" +
                "a7df9b2c-8455-3303-bffa-0a9ce32b88dd|ZDJCLX58824100FK|（201910存量自带机）承诺使用588元及以上4G套餐24个月每月减免100元话费（非插卡类）|588元/月|24|2400\n" +
                "f42fdb6a-92d9-39a3-9ea4-c3c3df8fecc8|ZDJJT881240FK|（家庭最低消费199元）承诺使用88元及以上4G套餐12个月每月减免40元话费（非插卡类）|88元/月|12|480\n" +
                "768b9cd8-9597-3313-a0ba-a3a8fbc3220a|ZDJJT881240K|（家庭最低消费199元）承诺使用88元及以上4G套餐12个月每月减免40元话费（插卡类）|88元/月|12|480\n" +
                "5ac62423-1e34-3162-ae3d-8168cad72b82|ZDJJT882440FK|（家庭最低消费199元）承诺使用88元及以上4G套餐24个月每月减免40元话费（非插卡类）|88元/月|24|960\n" +
                "959e1b72-d46f-36d6-b037-ecc1cc0bca16|ZDJJT882440K|（家庭最低消费199元）承诺使用88元及以上4G套餐24个月每月减免40元话费（插卡类）|88元/月|24|960\n" +
                "a1632956-17ee-3f9b-a1ee-d70c40aa4791|ZDJJT1381260FK|（家庭最低消费299元）承诺使用138元及以上4G套餐12个月每月减免60元话费（非插卡类）|138元/月|12|720\n" +
                "0a008dcd-729a-3088-aa83-21c5f6359a99|ZDJJT1381260K|（家庭最低消费299元）承诺使用138元及以上4G套餐12个月每月减免60元话费（插卡类）|138元/月|12|720\n" +
                "cea81f39-81d3-3811-bbb6-73bec3df7edd|ZDJJT1382460FK|（家庭最低消费299元）承诺使用138元及以上4G套餐24个月每月减免60元话费（非插卡类）|138元/月|24|1440\n" +
                "f8007f60-445e-3326-a504-c5cfa97594fa|ZDJJT1382460K|（家庭最低消费299元）承诺使用138元及以上4G套餐24个月每月减免60元话费（插卡类）|138元/月|24|1440\n" +
                "0d1fad94-5a0d-3231-9939-57dfc30bc790|ZDJJT1881280FK|（家庭最低消费399元）承诺使用188元及以上4G套餐12个月每月减免80元话费（非插卡类）|188元/月|12|960\n" +
                "4abe3876-29fd-3213-87bf-282c908ce15c|ZDJJT1881280K|（家庭最低消费399元）承诺使用188元及以上4G套餐12个月每月减免80元话费（插卡类）|188元/月|12|960\n" +
                "cb42694b-6cc2-34e3-842c-aede977a0d18|ZDJJT1882480FK|（家庭最低消费399元）承诺使用188元及以上4G套餐24个月每月减免80元话费（非插卡类）|188元/月|24|1920\n" +
                "01430bd9-996c-3deb-9fc1-c57484927d59|ZDJJT1882480K|（家庭最低消费399元）承诺使用188元及以上4G套餐24个月每月减免80元话费（插卡类）|188元/月|24|1920\n" +
                "78ead653-eee2-3e06-9377-048aa491d8be|ZDJJT23812120FK|（家庭最低消费599元）承诺使用288元及以上4G套餐12个月每月减免120元话费（非插卡类）|288元/月|12|1440\n" +
                "27e0c72b-add1-35a2-af9a-b6ffbce2ba95|ZDJJT23812120K|（家庭最低消费599元）承诺使用288元及以上4G套餐12个月每月减免120元话费（插卡类）|288元/月|12|1440\n" +
                "4d4a04cb-eed7-3d24-8b2e-139963b9533e|ZDJJT23824120FK|（家庭最低消费599元）承诺使用288元及以上4G套餐24个月每月减免120元话费（非插卡类）|288元/月|24|2880\n" +
                "a3e0043f-280c-3119-a35d-18f7807eac6f|ZDJJT23824120K|（家庭最低消费599元）承诺使用288元及以上4G套餐24个月每月减免120元话费（插卡类）|288元/月|24|2880\n" +
                "e2c9c4ac-5ccd-3471-bbe7-0d0bb77f5024|XYHFQ5820FK|（201910校园）承诺使用58元学而思套餐24个月每月减免20元话费（非插卡类）|58元/月|24|480\n" +
                "5f143057-a0a4-3384-99c4-e1343ea7d231|XYHFQ5820K|（201910校园）承诺使用58元学而思套餐24个月每月减免20元话费（插卡类）|58元/月|24|480\n" +
                "ec756e64-8cfe-3768-9c49-397fe4c4c469|XYHFQ8820FK|（201910校园）承诺使用88元及以上4G套餐24个月每月减免20元话费（非插卡类）|88元/月|24|480\n" +
                "68343949-9012-34b9-8aa9-908e57791358|XYHFQ8820K|（201910校园）承诺使用88元及以上4G套餐24个月每月减免20元话费（插卡类）|88元/月|24|480\n" +
                "c1806f9f-3571-38bc-8f05-5f150584eebb|XYHFQ13830FK|（201910校园）承诺使用138元及以上4G套餐24个月每月减免30元话费（非插卡类）|138元/月|24|720\n" +
                "650f1fd3-0953-3b44-865f-2e1735912efa|XYHFQ13830K|（201910校园）承诺使用138元及以上4G套餐24个月每月减免30元话费（插卡类）|138元/月|24|720\n" +
                "7a17874b-707a-3223-b360-ac2ad489430e|XYHFQ305FK|（201910校园）承诺使用30元学而思学币包24个月每月减免5元话费（非插卡类）|30元/月|24|120\n" +
                "2d5c1165-2572-3487-b9c7-de52c683bfbe|XYHFQ305K|（201910校园）承诺使用30元学而思学币包24个月每月减免5元话费（插卡类）|30元/月|24|120\n";
        String[] setMeals = s.split("\n");
        List<SetMeal> mealList = new ArrayList<>();
        StringBuffer insert = new StringBuffer();
        StringBuffer update = new StringBuffer();
        for (String sm:setMeals){
            String[] strings = sm.split("\\|");
            SetMeal meal = new SetMeal();
            meal.setPackageNo(strings[0]);
            meal.setTradeMark(strings[1]);
            meal.setTitle(strings[2]);
            meal.setTotalAmount(strings[5]);
            meal.setNum(Integer.valueOf(strings[4]));
            meal.setAmount(MoneyUtil.divide(meal.getTotalAmount(),meal.getNum().toString()));
            meal.setAppId("201910091625131208151");
            if (meal.getNum() == 24){
                meal.setState(1);
            }else {
                meal.setState(0);
            }
            SetMeal setMeal = setMealService.queryByPackageNo(meal.getPackageNo());
            if (setMeal!= null){
                meal.setId(setMeal.getId());
                setMealService.updateById(meal);
                update.append(meal.getId()).append(",");
            }else {
                setMealService.insert(meal);
                insert.append(meal.getId()).append(",");
            }
            mealList.add(meal);
        }
        System.err.println(JSONObject.toJSONString(mealList));
        System.err.println(insert.toString());
        System.err.println(update.toString());
    }


    @Test
    public void refund(){
        PayOrder order = payOrderService.queryByOutOrderNo( "76403234672473");
        if (order == null){
            return;
        }
        Refund refund = new Refund();
        refund.setOutTradeNo(order.getOutTradeNo());
        refund.setAmount(order.getAmount());
        refund.setOutRequestNo(StringUtils.getTradeNo());
        refund.setReason("业务回退");
        refund.setType(0);
        RefundResult result = authorizePayService.refund(refund);
        if (result.isSuccess()){
            myBankSupplyChainService.tradeRepay(order.getAuthNo());
        }
        System.err.println(result);
    }

    @Test
    public void jieqing(){
        List<SetMeal> mealList = setMealService.queryList(new SetMeal());
        for (SetMeal meal:mealList){
            meal.setRedPackState(1);
            int i = new BigDecimal(meal.getTotalAmount()).compareTo(new BigDecimal(800));
            int j = new BigDecimal(meal.getTotalAmount()).compareTo(new BigDecimal(1000));
            if (i ==-1){
                meal.setRedPackAmount("15.00");
            }
            if ((i==1 || i==0) && j == -1 ){
                meal.setRedPackAmount("25.00");
            }
            if (j==0 || j == 1 ){
                meal.setRedPackAmount("30.00");
            }
            setMealService.updateById(meal);
        }

    }

    @Test
    public void refunds(){
        PayOrder order = payOrderService.queryById(53l);
        SendMessage.sendMessage(JmsMessaging.ORDER_NOTIFY_MESSAGE,JSONObject.toJSONString(order));
    }


    /**
     * 查询是否有正在审核中的商户
     *
     * @param dto
     * @return
     */
    private AuthorizeMerchant createAuthorizeMerchant(RegisterMerchant dto) {
        AuthorizeMerchant queryMerchant = new AuthorizeMerchant();
        queryMerchant.setMerchantNo(dto.getMerchantNo());
        List<AuthorizeMerchant>  merchantList = authorizeMerchantService.queryList(queryMerchant);
        if (DataUtil.isNotEmpty(merchantList)){
            boolean flag = false;
            for (AuthorizeMerchant merchant:merchantList){
                if (merchant.isSuccess()){
                    flag = true;
                }
            }
            if (flag){
                return null;
            }
        }
        AuthorizeMerchant merchant = new AuthorizeMerchant();
        merchant.setAppId(dto.getAppId());
        merchant.setWayId(dto.getWayId());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setCreateTime(new Date());
        merchant.setName(dto.getName());
        merchant.setOperatorName(dto.getOperatorName());
        merchant.setStoreSubjectName(dto.getStoreSubjectName());
        merchant.setStoreSubjectCertNo(dto.getStoreSubjectCertNo());
        merchant.setStoreNo(dto.getStoreNo());
        merchant.setStoreName(dto.getStoreName());
        merchant.setStoreProvince(dto.getStoreProvince());
        merchant.setStoreCity(dto.getStoreCity());
        merchant.setStoreCounty(dto.getStoreCounty());
        merchant.setStoreProvinceCode(dto.getStoreProvinceCode());
        merchant.setStoreCityCode(dto.getStoreCityCode());
        merchant.setStoreCountyCode(dto.getStoreCountyCode());
        merchant.setSellerNo(dto.getSellerNo());
        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
        authorizeMerchantService.insert(merchant);
        System.err.println(merchant.getId());
        return merchant;
    }


    /**
     * 签约商户
     *
     * @param merchant
     * @return （原因）没有返回，则为签约成功
     */
    private void createSupplier(AuthorizeMerchant merchant) {
        AuthorizeMerchant oldMerchant = authorizeMerchantService.queryByAliPayLoginNo(merchant.getSellerNo());
        if (oldMerchant != null ) {
            if (oldMerchant.getState().equals(AuthorizeMerchant.State.success.getCode())){
                merchant.setSupplierNo(oldMerchant.getSupplierNo());
                merchant.setState(AuthorizeMerchant.State.success.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return ;
            }else if (oldMerchant.getState().equals(AuthorizeMerchant.State.failed.getCode())){
                merchant.setReason(oldMerchant.getReason());
                merchant.setState(AuthorizeMerchant.State.failed.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return ;
            }
        }
        if ( oldMerchant == null || (oldMerchant!= null && oldMerchant.getState().equals(AuthorizeMerchant.State.waiting.getCode()))){
            AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
            MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
            if (response.isSuccess()) {
                merchant.setSupplierNo(response.getSupplierNo());
                merchant.setState(AuthorizeMerchant.State.success.getCode());
                merchant.setFinishTime(new Date());
            } else {
                merchant.setReason(response.getSubMsg());
                merchant.setState(AuthorizeMerchant.State.failed.getCode());
                merchant.setFinishTime(new Date());
            }
            authorizeMerchantService.updateById(merchant);
            return ;
        }
    }


    private void updateSupplier(AuthorizeMerchant merchant) {
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
        if (response.isSuccess()) {
            merchant.setSupplierNo(response.getSupplierNo());
            merchant.setState(AuthorizeMerchant.State.success.getCode());
            merchant.setFinishTime(new Date());
            authorizeMerchantService.updateById(merchant);
        } else {
            merchant.setReason(response.getSubMsg());
            merchant.setState(AuthorizeMerchant.State.failed.getCode());
            merchant.setFinishTime(new Date());
            System.err.println(response.getSubMsg());
        }
    }


    /**
     * 创建商户
     *
     * @param merchant
     * @return
     */
    private SuppilerCreate create(AuthorizeMerchant merchant) {
        SuppilerCreate create = new SuppilerCreate();
        create.setStoreNo(merchant.getSellerNo());
        create.setSellerName(merchant.getName());
        create.setRcvContactEmail(null);
        create.setRcvLoginId(merchant.getSellerNo());
        create.setRcvContactName(merchant.getContactName());
        create.setRcvContactPhone(merchant.getContactPhone());
        create.setOperatorName(merchant.getOperatorName());
        create.setStoreNo(merchant.getStoreNo());
        create.setStoreName(merchant.getStoreName());
        create.setStoreSubjectName(merchant.getStoreSubjectName());
        create.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        create.setStoreProvince(merchant.getStoreProvince());
        create.setStoreCity(merchant.getStoreCity());
        create.setStoreCounty(merchant.getStoreCounty());
        return create;
    }
}

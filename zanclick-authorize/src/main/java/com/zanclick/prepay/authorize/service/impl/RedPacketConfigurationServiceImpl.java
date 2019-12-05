package com.zanclick.prepay.authorize.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.RedPacketConfiguration;
import com.zanclick.prepay.authorize.mapper.RedPacketConfigurationMapper;
import com.zanclick.prepay.authorize.service.RedPacketConfigurationService;
import com.zanclick.prepay.authorize.util.MoneyUtil;
import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author panliang
 * @Date 2019/12/2 10:47
 * @Description //
 **/
@Slf4j
@Service
public class RedPacketConfigurationServiceImpl extends BaseMybatisServiceImpl<RedPacketConfiguration, Long> implements RedPacketConfigurationService {

    @Autowired
    private RedPacketConfigurationMapper redPackBlacklistMapper;

    @Override
    protected BaseMapper<RedPacketConfiguration, Long> getBaseMapper() {
        return redPackBlacklistMapper;
    }

    @Override
    public String queryRedPacketAmount(String aliPayLoginNo, String cityCode, String provinceCode, String amount, Integer num) {
        RedPacketConfiguration configuration = queryByLevelAndNameCode(RedPacketConfiguration.Level.shopLevel.getCode(), aliPayLoginNo);
        if (configuration != null && RedPacketConfiguration.Status.open.getCode().equals(configuration.getStatus())) {
            return getAmount(configuration.getAmountInfo(),amount,num);
        }
        configuration = queryByLevelAndNameCode(RedPacketConfiguration.Level.cityLevel.getCode(), cityCode);
        if (configuration != null && RedPacketConfiguration.Status.open.getCode().equals(configuration.getStatus())) {
            return getAmount(configuration.getAmountInfo(),amount,num);
        }
        configuration = queryByLevelAndNameCode(RedPacketConfiguration.Level.provinceLevel.getCode(), provinceCode);
        if (configuration != null && RedPacketConfiguration.Status.open.getCode().equals(configuration.getStatus())) {
            return getAmount(configuration.getAmountInfo(),amount,num);
        }
        return null;
    }

    private String getAmount(String jsonStr,String amount,Integer num){
        JSONArray array = JSONArray.parseArray(jsonStr);
        for (int i = 0 ; i<array.size() ; i++){
            JSONObject object = array.getJSONObject(i);
            String min = object.getString("min");
            String max = object.getString("max");
            Integer fqNum = object.getInteger("num");
            String money = object.getString("money");
            if (MoneyUtil.judgeMoney(amount,min) && MoneyUtil.largeMoney(max,amount) && fqNum.equals(num)){
                return MoneyUtil.formatMoney(money);
            }
        }
        return null;
    }


    /**
     * 根据级别和code查询
     *
     * @param level
     * @param nameCode
     */
    private RedPacketConfiguration queryByLevelAndNameCode(Integer level, String nameCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("level", level);
        params.put("nameCode", nameCode);
        return redPackBlacklistMapper.selectByLevelAndNameCode(params);
    }
}

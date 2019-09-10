package com.zanclick.prepay.authorize.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.authorize.entity.AuthorizeOrder;
import com.zanclick.prepay.authorize.entity.AuthorizeOrderRecord;
import com.zanclick.prepay.authorize.mapper.AuthorizeOrderRecordMapper;
import com.zanclick.prepay.authorize.service.AuthorizeOrderRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 预授权订单预期还款计划
 *
 * @author duchong
 * @date 2019-7-8 15:28:06
 **/
@Service
public class AuthorizeOrderRecordServiceImpl extends BaseMybatisServiceImpl<AuthorizeOrderRecord, Long> implements AuthorizeOrderRecordService {

    @Autowired
    private AuthorizeOrderRecordMapper authorizeOrderRecordMapper;


    @Override
    protected BaseMapper<AuthorizeOrderRecord, Long> getBaseMapper() {
        return authorizeOrderRecordMapper;
    }

    @Override
    public List<AuthorizeOrderRecord> queryByAuthNo(String authNo) {
        return authorizeOrderRecordMapper.selectByAuthNo(authNo);
    }

    @Override
    public List<AuthorizeOrderRecord> queryWaitByAuthNo(String authNo) {
        AuthorizeOrderRecord query = new AuthorizeOrderRecord();
        query.setAuthNo(authNo);
        query.setState(AuthorizeOrderRecord.State.wait.getCode());
        return this.queryList(query);
    }


    @Override
    public List<AuthorizeOrderRecord> queryByTradeNo(String tradeNo) {
        AuthorizeOrderRecord query = new AuthorizeOrderRecord();
        query.setTradeNo(tradeNo);
        return this.queryList(query);
    }

    @Override
    public List<AuthorizeOrderRecord> queryByAuthNoAndNum(String authNo, Integer num) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("authNo", authNo);
        if (num != 0){
            params.put("num", num);
        }
        return authorizeOrderRecordMapper.selectByAuthNoAndNum(params);
    }


    @Override
    public void handleRecordList(List<AuthorizeOrderRecord> recordList, Integer type, String tradeNo) {
        if (DataUtil.isEmpty(recordList)){
            return;
        }
        Integer state = type == 0 ? AuthorizeOrderRecord.State.unfreed.getCode() : AuthorizeOrderRecord.State.payed.getCode();
        for (AuthorizeOrderRecord record:recordList){
            record.setRealTime(new Date());
            record.setTradeNo(tradeNo);
            record.setState(type);
            this.updateById(record);
        }
    }

    @Override
    public boolean queryListRecord(String authNo, Integer num) {
        if (num == 0){
            return true;
        }
        Integer maxNum = authorizeOrderRecordMapper.selectMaxNum(authNo);
        if (maxNum<=num){
            return true;
        }
        return false;
    }

    @Override
    public List<AuthorizeOrderRecord> createAuthorizeOrderRecord(AuthorizeOrder order) {
        List<AuthorizeOrderRecord> recordList = new ArrayList<>();
        AuthorizeOrderRecord record = null;
        for (int i = 0; i < order.getFee().getCycle(); i++) {
            record = new AuthorizeOrderRecord();
            record.setAuthNo(order.getAuthNo());
            record.setExpectTime(DateUtil.addMonth(order.getSettleDate(), i));
            record.setNum(i + 1);
            record.setState(AuthorizeOrderRecord.State.wait.getCode());
            record.setCreateTime(new Date());
            if (record.getNum().equals(order.getFee().getCycle())) {
                record.setMoney(order.getFee().getFirstMoney());
            } else {
                record.setMoney(order.getFee().getEachMoney());
            }
            recordList.add(record);
        }
        this.insertBatch(recordList);
        return recordList;
    }
}

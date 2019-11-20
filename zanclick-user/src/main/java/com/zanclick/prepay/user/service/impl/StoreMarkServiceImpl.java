package com.zanclick.prepay.user.service.impl;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.common.base.service.impl.BaseMybatisServiceImpl;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.StringUtils;
import com.zanclick.prepay.user.entity.StoreMark;
import com.zanclick.prepay.user.mapper.StoreMarkMapper;
import com.zanclick.prepay.user.service.StoreMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Administrator
 * @date 2019-11-20 15:52:13
 **/
@Service
public class StoreMarkServiceImpl extends BaseMybatisServiceImpl<StoreMark, Long> implements StoreMarkService {

    @Autowired
    private StoreMarkMapper storeMarkMapper;


    @Override
    protected BaseMapper<StoreMark, Long> getBaseMapper() {
        return storeMarkMapper;
    }

    @Override
    public StoreMark queryByAliPayLoginNo(String aliPayLoginNo) {
        return storeMarkMapper.selectByAliPayLoginNo(aliPayLoginNo);
    }

    @Override
    public StoreMark createStoreMark(String aliPayLoginNo, String name) {
        StoreMark mark = this.queryByAliPayLoginNo(aliPayLoginNo);
        if (DataUtil.isNotEmpty(mark)) {
            return mark;
        }
        mark = new StoreMark();
        mark.setAliPayLoginNo(aliPayLoginNo);
        mark.setCode(StringUtils.createRandomStr());
        mark.setCreateTime(new Date());
        mark.setName(name);
        getBaseMapper().insert(mark);
        return mark;
    }
}

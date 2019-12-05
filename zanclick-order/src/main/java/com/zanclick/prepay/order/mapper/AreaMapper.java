package com.zanclick.prepay.order.mapper;

import com.zanclick.prepay.common.base.dao.mybatis.BaseMapper;
import com.zanclick.prepay.order.entity.Area;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author admin
 * @date 2019-12-02 16:24:45
 **/
@Mapper
public interface AreaMapper extends BaseMapper<Area,Long> {

    List<Area> selectByLevel(Integer level);

}

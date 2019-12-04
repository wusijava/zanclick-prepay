package com.zanclick.prepay.order.service;

import com.zanclick.prepay.common.base.service.BaseService;
import com.zanclick.prepay.order.entity.Area;

/**
 * @author admin
 * @date 2019-12-02 16:24:45
 **/
public interface AreaService extends BaseService<Area,Long> {
    Area selectByName(Area name);
}

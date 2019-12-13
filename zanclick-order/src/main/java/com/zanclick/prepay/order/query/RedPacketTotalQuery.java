package com.zanclick.prepay.order.query;

import com.zanclick.prepay.order.entity.RedPacketTotal;
import lombok.Data;

/**
 * @ Description   :  红包核算查询
 * @ Author        :  wusi
 * @ CreateDate    :  2019/12/13$ 14:48$
 */
@Data
public class RedPacketTotalQuery extends RedPacketTotal {
    private Integer page;

    private Integer limit;

    private Integer offset;
    private String startTime;

    private String endTime;
}

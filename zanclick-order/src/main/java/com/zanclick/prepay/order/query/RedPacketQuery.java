package com.zanclick.prepay.order.query;

import com.zanclick.prepay.order.entity.RedPacket;
import lombok.Data;

/**
 * @ Description   :  红包查询
 * @ Author        :  wusi
 * @ CreateDate    :  2019/12/12$ 16:24$
 */
@Data
public class RedPacketQuery extends RedPacket {

    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;
}

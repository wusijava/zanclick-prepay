package com.zanclick.prepay.order.query;

import com.zanclick.prepay.order.entity.RedPacket;
import lombok.Data;

/**
 * @ Description   :  java类作用描述
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/13$ 18:35$
 */
@Data
public class RedPacketQuery extends RedPacket {
    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;
}

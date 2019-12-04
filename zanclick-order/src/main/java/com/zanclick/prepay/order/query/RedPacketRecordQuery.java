package com.zanclick.prepay.order.query;

import com.zanclick.prepay.order.entity.RedPacketRecord;
import lombok.Data;

/**
 * @ Description   :  java类作用描述
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/13$ 18:35$
 */
@Data
public class RedPacketRecordQuery extends RedPacketRecord {
    private Integer page;

    private Integer limit;

    private Integer offset;
    private String startTime;

    private String endTime;

}

package com.zanclick.prepay.authorize.query;

import com.zanclick.prepay.authorize.entity.RedPackBlacklist;
import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/11/22 11:40
 * @Description //
 **/
@Data
public class RedPackBlacklistQuery extends RedPackBlacklist {

    private Integer page;

    private Integer limit;

    private Integer offset;

}

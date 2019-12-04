package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/11/22 11:16
 * @Description 可领红包黑名单
 **/
@Data
public class RedPackBlacklist implements Identifiable<Long> {

    /**
     * id
     */
    private Long id;

    /**
     * 支付宝账号
     */
    private String sellerNo;

    /**
     * 账号名称
     **/
    private String name;

    private Integer type;

    /**
     * 创建时间
     */
    private String createTime;

    public enum RedPackType {
        personal(0, "个人"),
        settleDay(1, "日结"),
        settleWeek(2, "周结"),
        settleMonth(3, "月结");

        private Integer code;
        private String desc;

        RedPackType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}

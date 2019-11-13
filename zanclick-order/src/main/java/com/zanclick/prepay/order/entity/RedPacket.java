package com.zanclick.prepay.order.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 资金授权商户
 *
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class RedPacket implements Identifiable<Long> {

    private Long id;

    private String amount;

    private String orderNo;

    private Date createTime;

    private String receiveNo;

    private String appId;

    private Integer state;

    private String reason;

    public enum State {
        success(1, "领取成功"),
        fail(-1, "领取失败"),
        wait(0, "等待领取");

        private Integer code;
        private String desc;

        State(Integer code, String desc) {
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

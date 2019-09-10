package com.zanclick.prepay.app.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * 应用信息
 *
 * @author duchong
 * @date 2019-9-10 14:46:38
 */
@Data
public class AppInfo implements Identifiable<Long> {

    private Long id;

    private String appId;

    private String appName;

    private String key;

    private String createTime;

    private Integer state;

    public enum State {
        open(1, "开启的"),
        close(0, "关闭的");

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

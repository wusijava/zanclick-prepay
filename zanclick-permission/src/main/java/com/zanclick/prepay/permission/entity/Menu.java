package com.zanclick.prepay.permission.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description 主菜单
 * @date 2019-11-13 14:31:08
 */
@Data
public class Menu implements Identifiable<Long> {

    private Long id;

    private String title;

    private String icon;

    private String path;

    private String name;

    private String code;

    private String homeCode;

    private Integer type;

    private Date createTime;

    private String component;

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

    public enum Type {
        menu(1, "菜单"),
        btn(2, "按钮");

        private Integer code;
        private String desc;

        Type(Integer code, String desc) {
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

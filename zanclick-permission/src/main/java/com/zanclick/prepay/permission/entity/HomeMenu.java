package com.zanclick.prepay.permission.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author duchong
 * @description 主菜单
 * @date 2019-11-13 14:31:25
 */
@Data
public class HomeMenu implements Identifiable<Long> {

    private Long id;

    private String title;

    private String name;

    private String icon;

    private String code;

    private Date createTime;

    private List<Menu> submenus;

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

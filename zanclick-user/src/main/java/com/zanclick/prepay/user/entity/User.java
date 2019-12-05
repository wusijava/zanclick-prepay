package com.zanclick.prepay.user.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @author duchong
 * @description 用户
 * @date 2019-8-3 10:05:37
 */
@Data
public class User implements Identifiable<Long> {

    private Long id;

    private String uid;

    private String username;

    private String password;

    private String mobile;

    private String nickName;

    private Date createTime;

    private Integer state;

    private Integer type;

    private String salt;

    private String storeMarkCode;

    private String cityCode;

    private String provinceCode;

    private String pwd;

    public enum State {
        /**
         * 开启
         */
        OPEN(1),
        /**
         * 关闭
         */
        CLOSE(0);

        private Integer code;

        State(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }


    public enum Type {
        /**
         * 管理员
         */
        ADMIN(0),
        /**
         * 门店角色
         */
        USER(1),
        /**
         * 商户角色
         */
        MANAGE(2),
        /**
         * 市级账号
         * */
        CITY(3);

        private Integer code;

        Type(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }
}

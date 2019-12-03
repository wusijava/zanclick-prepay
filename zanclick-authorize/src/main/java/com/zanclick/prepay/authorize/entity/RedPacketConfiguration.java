package com.zanclick.prepay.authorize.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.Data;

/**
 * @Author panliang
 * @Date 2019/12/2 10:32
 * @Description //红包配置实体类
 **/
@Data
public class RedPacketConfiguration implements Identifiable<Long> {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     **/
    private String name;

    /**
     * 名称code
     **/
    private String nameCode;

    /**
     * 级别(1.门店  2.市  3.省)
     **/
    private Integer level;

    /**
     * 状态:1启用/0关闭
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    public enum Level{

        shopLevel(1,"门店"),
        cityLevel(2,"市"),
        provinceLevel(3,"省");

        private Integer code;
        private String desc;

        Level(Integer  code,String desc){
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

    public String getLevelDesc(){
        if(Level.shopLevel.getCode().equals(level)){
            return Level.shopLevel.getDesc();
        }else if(Level.cityLevel.getCode().equals(level)){
            return Level.cityLevel.getDesc();
        }else{
            return Level.provinceLevel.getDesc();
        }
    }

    public enum Status{

        open(1, "开启"),
        closed(0, "关闭");

        private Integer code;
        private String desc;

        Status(Integer code, String desc) {
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

    public String getStatusDesc(){
        if(Status.open.getCode().equals(status)){
            return Status.open.getDesc();
        }else{
            return Status.closed.getDesc();
        }
    }

}

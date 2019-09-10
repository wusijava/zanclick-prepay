package com.zanclick.prepay.common.utils.qrcode;

import java.io.Serializable;

/**
 * 二维码相关
 *
 * @author duchong
 * @date 2019-8-26 15:03:48
 */
public class QrModel implements Serializable {

    private String url;

    private String name;

    private String type = "png";

    private Integer height = 1080;

    private Integer width = 1080;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}

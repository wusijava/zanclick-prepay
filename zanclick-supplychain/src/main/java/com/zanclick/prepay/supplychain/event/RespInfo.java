package com.zanclick.prepay.supplychain.event;

import lombok.Data;

/**
 * @author lvlu
 * @date 2019-05-10 15:32
 **/
@Data
public class RespInfo {
    private String respCode;

    private String respInfo;

    private RespInfo() {
    }

    private RespInfo(String respCode, String respInfo) {
        this.respCode = respCode;
        this.respInfo = respInfo;
    }

    public static RespInfo success(){
        return new RespInfo("0000","接收消息成功");
    }

    public static RespInfo fail(String msg){
        return new RespInfo("1609",msg);
    }


}

package com.zanclick.prepay.common.entity;
/**
 * Created by lvlu on 2017/12/19.
 */

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口响应
 *
 * @author lvlu
 * @date 2017-12-19 10:08
 **/
@Data
public class Response<T> implements Serializable{

    private String code;

    private String msg;

    private T data;

    private Response() {
    }

    private Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Response<T> ok(T data){
        return response(ResponseCode.SUCCESS, ResponseMsg.SUCCESS,data);
    }
    public static <T> Response<T> ok(String msg,T data){
        return response(ResponseCode.SUCCESS,msg,data);
    }

    public static <T> Response<T> fail(T data){
        return response(ResponseCode.FAIL,null,data);
    }
    public static <T> Response<T> fail(String msg){
        return response(ResponseCode.FAIL,msg,null);
    }
    public static <T> Response<T> fail(String msg,T data){
        return response(ResponseCode.FAIL,msg,data);
    }

    public static <T> Response<T> response(String code, String msg,T data){
        Response<T> response = new Response<>(code,msg,data);
        return response;
    }

    static class ResponseCode {
        public static String SUCCESS = "20000";
        public static String FAIL = "40500";
    }

    static class ResponseMsg {
        public static String SUCCESS = "SUCCESS";
    }

    public boolean isSuccess() {
        if (ResponseCode.SUCCESS.equals(this.code)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        Response response = new Response();
        response.setCode("20000");
        response.setMsg("查询成功");
        JSONObject object = new JSONObject();
        object.put("orderNo",StringUtils.getTradeNo());
        object.put("outOrderNo",StringUtils.getTradeNo());
        response.setData(object);
        System.err.println(JSONObject.toJSONString(response));
    }
}

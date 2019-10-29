package com.zanclick.prepay.authorize.exception;

/**
 * @author lvlu
 * @date 2019-05-10 12:00
 **/
public class SupplyChainException extends RuntimeException{

    private String code;

    private String msg;

    private String  requestId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public SupplyChainException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public SupplyChainException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public SupplyChainException(String code, String msg, String  requestId) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.requestId = requestId;
    }

    public SupplyChainException(Throwable cause) {
        super(cause);
    }

    public SupplyChainException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public SupplyChainException(String code, String msg, String  requestId, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
        this.requestId = requestId;
    }

}

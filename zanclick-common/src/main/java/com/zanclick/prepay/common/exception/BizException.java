package com.zanclick.prepay.common.exception;

import lombok.Data;

/**
 * @author lvlu
 * @date 2019-02-21 16:47
 **/
@Data
public class BizException extends RuntimeException {

    private String code;

    private String message;

    public BizException(String code,String message,Throwable throwable){
        super(message,throwable);
        this.code = code;
        this.message = message;
    }

    public BizException(String code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String message){
        super(message);
        this.message = message;
    }

    public BizException(Throwable throwable){
        super(throwable);
    }

}

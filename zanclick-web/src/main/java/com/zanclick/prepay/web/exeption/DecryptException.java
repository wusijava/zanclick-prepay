package com.zanclick.prepay.web.exeption;

import com.zanclick.prepay.common.exception.BizException;

public class DecryptException extends BizException {

    public DecryptException(String code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public DecryptException(String code, String message) {
        super(code, message);
    }

    public DecryptException(String message) {
        super(message);
    }

    public DecryptException(Throwable throwable) {
        super(throwable);
    }
}

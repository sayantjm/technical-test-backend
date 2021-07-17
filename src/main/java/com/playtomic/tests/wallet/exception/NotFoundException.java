package com.playtomic.tests.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Juanma Perales on 17/7/21
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Wallet not found")
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7558395042907344117L;

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }

}

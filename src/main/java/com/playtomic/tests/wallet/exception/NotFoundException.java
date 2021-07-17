package com.playtomic.tests.wallet.exception;

/**
 * Created by Juanma Perales on 17/7/21
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7558395042907344117L;

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }

}

package com.playtomic.tests.wallet.exception;

/**
 * Created by Juanma Perales on 17/7/21
 */
public class FoundException extends RuntimeException {

    private static final long serialVersionUID = -8273019063474530364L;

    public FoundException(String msg) {
        super(msg);
    }

    public FoundException(String msg, Exception ex) {
        super(msg, ex);
    }

}

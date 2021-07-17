package com.playtomic.tests.wallet.exception;

/**
 * Created by Juanma Perales on 17/7/21
 */
public class AmountException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AmountException(String msg) {
        super(msg);
    }

    public AmountException(String msg, Exception ex) {
        super(msg, ex);
    }
}

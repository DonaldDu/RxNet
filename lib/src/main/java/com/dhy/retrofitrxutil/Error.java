package com.dhy.retrofitrxutil;

public class Error implements IError {
    private final int code;
    private final String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
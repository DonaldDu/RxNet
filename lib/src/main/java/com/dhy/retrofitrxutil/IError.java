package com.dhy.retrofitrxutil;

public interface IError {
    int getCode();

    int httpCode();

    String getMessage();
}

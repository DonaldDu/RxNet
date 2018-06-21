package com.dhy.retrofitrxutil;

public interface IResponseStatus {
    int LOCALL_ERROR = -1;

    int getCode();

    String getMessage();

    String getDebugMessage();

    boolean isSuccess();
}

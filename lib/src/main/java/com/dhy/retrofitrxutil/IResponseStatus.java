package com.dhy.retrofitrxutil;

public interface IResponseStatus extends IError {
    int LOCALL_ERROR = -1;

    String getDebugMessage();

    boolean isSuccess();
}

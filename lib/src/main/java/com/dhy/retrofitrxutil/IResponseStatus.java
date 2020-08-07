package com.dhy.retrofitrxutil;

public interface IResponseStatus extends IError {
    int LOCAL_ERROR = -1;

    boolean isSuccess();
}

package com.dhy.retrofitrxutil;

public class ThrowableBZ extends Throwable {
    public final IResponseStatus status;

    public ThrowableBZ(IResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}

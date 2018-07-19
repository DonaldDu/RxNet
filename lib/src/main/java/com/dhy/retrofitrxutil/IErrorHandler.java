package com.dhy.retrofitrxutil;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

public interface IErrorHandler {
    void onError(ObserverWithBZ observer, Throwable e);

    Dialog showDialog(Context context, String msg);

    @NonNull
    IError parseError(Throwable e);

    boolean isAuthorizeFailed(Context context, int errorCode);

    void onLogout(Context context);
}

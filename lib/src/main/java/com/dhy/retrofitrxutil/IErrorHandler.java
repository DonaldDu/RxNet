package com.dhy.retrofitrxutil;

import android.app.Dialog;
import android.content.Context;

public interface IErrorHandler {
    void onError(ObserverWithBZ observer, Throwable e);

    Dialog showDialog(Context context, String msg);

    int getErrorCode(Throwable e);

    boolean isAuthorizeFailed(Context context, int errorCode);

    void onLogout(Context context);
}

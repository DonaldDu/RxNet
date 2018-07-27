package com.dhy.retrofitrxutil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

public interface IErrorHandler {
    void onError(@NonNull IObserverX observer, @NonNull Throwable e);

    Dialog showDialog(@NonNull Context context, @NonNull String msg);

    @NonNull
    IError parseError(@NonNull Throwable e);

    boolean isAuthorizeFailed(@NonNull Context context, int errorCode);

    void onLogout(@NonNull Context context);

    void onActivityError(@NonNull Activity activity, @NonNull IError error);

    void onBackgroundError(@NonNull Context context, @NonNull IError error, @NonNull Throwable e);
}

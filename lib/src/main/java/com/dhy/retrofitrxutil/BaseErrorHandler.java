package com.dhy.retrofitrxutil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.widget.Toast;

import retrofit2.HttpException;

public abstract class BaseErrorHandler implements IErrorHandler {

    @Override
    public void onError(IObserverX observer, Throwable e) {
        observer.dismissProgress();
        IError error = parseError(e);
        final Context context = observer.getContext();
        if (context instanceof Activity) {
            Dialog dialog = showDialog(context, error.getMessage());
            if (isAuthorizeFailed(context, error.getCode())) {
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onLogout(context);
                    }
                });
            }
        } else {
            String msg = error.getMessage();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public IError parseError(Throwable e) {
        int code;
        String message;
        if (e instanceof ThrowableBZ) {
            ThrowableBZ bz = (ThrowableBZ) e;
            return bz.status;
        } else if (e instanceof HttpException) {
            code = ((HttpException) e).code();
            message = "HTTP " + code;
        } else {
            code = -1;
            String msg = e.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = e.getClass().getName();
            }
            message = msg;
        }
        return new Error(code, message);
    }
}

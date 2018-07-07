package com.dhy.retrofitrxutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import retrofit2.HttpException;

public class SampleErrorHandler implements IErrorHandler {
    private static final String TAG = IErrorHandler.class.getSimpleName();

    @Override
    public void onError(ObserverWithBZ observer, Throwable e) {
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
            if (context != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, msg);
            }
            e.printStackTrace();
        }
    }

    @Override
    public Dialog showDialog(Context context, String msg) {
        return new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", null).show();

    }

    @NonNull
    @Override
    public IError parseError(Throwable e) {
        Error error = new Error();
        if (e instanceof ThrowableBZ) {
            ThrowableBZ bz = (ThrowableBZ) e;
            return bz.status;
        } else if (e instanceof HttpException) {
            error.code = ((HttpException) e).code();
            error.message = "HTTP " + error.code;
        } else {
            error.code = -1;
            String msg = e.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = e.getClass().getName();
            }
            error.message = msg;
        }
        return error;
    }

    private static final int AUTHORIZE_FAILED = 9001;

    @Override
    public boolean isAuthorizeFailed(Context context, int errorCode) {
        return errorCode == AUTHORIZE_FAILED;
    }

    @Override
    public void onLogout(Context context) {
        String msg = "onLogout";
        if (context != null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.i(TAG, msg);
    }

    private static class Error implements IError {
        int code;
        String message;

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}

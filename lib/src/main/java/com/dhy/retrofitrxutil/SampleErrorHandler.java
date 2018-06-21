package com.dhy.retrofitrxutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import retrofit2.HttpException;

public class SampleErrorHandler implements IErrorHandler {
    private static final String TAG = IErrorHandler.class.getSimpleName();

    @Override
    public void onError(ObserverWithBZ observer, Throwable e) {
        observer.dismissProgress();
        final Context context = observer.getContext();
        if (context instanceof Activity) {
            String msg = e.getMessage();
            Dialog dialog = showDialog(context, msg);
            if (isAuthorizeFailed(context, getErrorCode(e))) {
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onLogout(context);
                    }
                });
            }
        } else {
            String msg = "Error Message: " + e.getMessage();
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

    @Override
    public int getErrorCode(Throwable e) {
        if (e instanceof ThrowableBZ) {
            return ((ThrowableBZ) e).status.getCode();
        } else if (e instanceof HttpException) {
            return ((HttpException) e).code();
        } else return -1;
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
}

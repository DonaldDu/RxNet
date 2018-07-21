package com.dhy.retrofitrxutil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SampleErrorHandler extends BaseErrorHandler {
    private static final String TAG = IErrorHandler.class.getSimpleName();

    @Override
    public Dialog showDialog(Context context, String msg) {
        return new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", null).show();

    }

    @Override
    public boolean isAuthorizeFailed(Context context, int errorCode) {
        return errorCode == 9001;
    }

    @Override
    public void onLogout(Context context) {
        String msg = "onLogout";
        if (context != null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.i(TAG, msg);
    }
}

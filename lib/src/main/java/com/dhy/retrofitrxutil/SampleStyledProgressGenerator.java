package com.dhy.retrofitrxutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class SampleStyledProgressGenerator implements StyledProgressGenerator {
    @Override
    public StyledProgress generate(final IObserverX observer) {
        return new StyledProgress() {
            final Context context = observer.getContext();

            @Override
            public void showProgress() {
                show(true);
            }

            @Override
            public void dismissProgress() {
                show(false);
            }

            Dialog dialog;
            Toast toast;

            @SuppressLint("ShowToast")
            private void show(boolean show) {
                if (context instanceof Activity) {
                    if (dialog == null) dialog = new ProgressDialog(context);
                    if (show) {
                        if (!dialog.isShowing()) dialog.show();
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                observer.cancel();
                            }
                        });
                    } else if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    if (toast == null) {
                        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                    }
                    toast.setText("showProgress: " + show);
                    toast.show();
                }
            }
        };
    }
}

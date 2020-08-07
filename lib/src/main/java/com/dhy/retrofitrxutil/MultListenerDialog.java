package com.dhy.retrofitrxutil;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MultListenerDialog extends Dialog {
    public MultListenerDialog(@NonNull Context context) {
        super(context);
    }

    public MultListenerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MultListenerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        if (listener instanceof MultOnDismissListener) {
            super.setOnDismissListener(listener);
        } else {
            Toast.makeText(getContext(), "use addOnDismissListener plz", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        if (listener instanceof MultOnCancelListener) {
            super.setOnCancelListener(listener);
        } else {
            Toast.makeText(getContext(), "use addOnCancelListener plz", Toast.LENGTH_LONG).show();
        }
    }
}

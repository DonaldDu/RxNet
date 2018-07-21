package com.dhy.retrofitrxutil;

import android.content.Context;
import android.support.annotation.NonNull;

public abstract class ObserverX<T> extends ObserverWithBZ<T> {
    public ObserverX(@NonNull Context context) {
        super(context);
    }

    public ObserverX(@NonNull Context context, boolean successOnly, boolean autoDismiss) {
        super(context, successOnly, autoDismiss);
    }
}

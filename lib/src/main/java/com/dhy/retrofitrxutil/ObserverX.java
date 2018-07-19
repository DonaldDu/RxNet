package com.dhy.retrofitrxutil;

import android.content.Context;
import android.support.annotation.Nullable;

public abstract class ObserverX extends ObserverWithBZ {
    public ObserverX(@Nullable Context context) {
        super(context);
    }

    public ObserverX(@Nullable Context context, boolean successOnly, boolean autoDismiss) {
        super(context, successOnly, autoDismiss);
    }
}

package com.dhy.retrofitrxutil;

import android.content.Context;
import android.support.annotation.NonNull;

public interface IObserverX {
    void dismissProgress();

    @NonNull
    Context getContext();

    void cancel();
}

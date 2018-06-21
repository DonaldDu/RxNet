package com.dhy.retrofitrxutil;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;

public interface IDisposable {
    void registerDisposable(@NonNull Context context, @NonNull Disposable disposable);

    void onComplete(Context context, @NonNull Disposable disposable);
}

package com.dhy.retrofitrxutil;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;

public interface IDisposableHandler extends IDisposable {

    void onDestroy(Context context);
}

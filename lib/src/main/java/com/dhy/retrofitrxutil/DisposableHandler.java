package com.dhy.retrofitrxutil;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public final class DisposableHandler implements IDisposableHandler {
    private final Map<Context, List<Disposable>> map = new HashMap<>();

    @Override
    public void registerDisposable(@NonNull Context context, @NonNull Disposable disposable) {
        if (!(context instanceof Application)) {
            List<Disposable> list = map.get(context);
            if (list == null) {
                list = new ArrayList<>();
                map.put(context, list);
            }
            list.add(disposable);
        }
    }

    @Override
    public void onComplete(Context context, @NonNull Disposable disposable) {
        if (!(context instanceof Application)) {
            List<Disposable> list = map.get(context);
            if (list != null) list.remove(disposable);
        }
    }

    public void onDestroy(Context context) {
        List<Disposable> list = map.remove(context);
        if (list != null) for (Disposable disposable : list) {
            disposable.dispose();
        }
    }
}

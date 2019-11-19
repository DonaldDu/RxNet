package com.dhy.retrofitrxtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dhy.retrofitrxutil.IDisposable;
import com.dhy.retrofitrxutil.IDisposableHandler;
import com.dhy.retrofitrxutil.ObserverX;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends AppCompatActivity implements IDisposable {
    private IDisposableHandler disposableHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposableHandler = App.getInstance(this).disposableHandler;
    }

    @Override
    public void registerDisposable(@NonNull Context context, @NonNull Disposable disposable) {
        disposableHandler.registerDisposable(context, disposable);
    }

    @Override
    public void onComplete(Context context, @NonNull Disposable disposable) {
        disposableHandler.onComplete(context, disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposableHandler.onDestroy(this);
    }

    static <T> Observable<T> subscribeAndroid(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static <T> ObservableTransformer<T, T> composeAndroid() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

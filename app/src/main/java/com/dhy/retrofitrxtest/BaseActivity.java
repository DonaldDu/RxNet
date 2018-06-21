package com.dhy.retrofitrxtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dhy.retrofitrxutil.IDisposable;
import com.dhy.retrofitrxutil.IDisposableHandler;

import io.reactivex.disposables.Disposable;

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
}

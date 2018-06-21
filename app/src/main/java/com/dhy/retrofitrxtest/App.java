package com.dhy.retrofitrxtest;

import android.app.Application;
import android.content.Context;

import com.dhy.retrofitrxutil.IDisposableHandler;
import com.dhy.retrofitrxutil.DisposableHandler;
import com.dhy.retrofitrxutil.ObserverWithBZ;
import com.dhy.retrofitrxutil.SampleErrorHandler;
import com.dhy.retrofitrxutil.SampleStyledProgressGenerator;

public class App extends Application {
    public IDisposableHandler disposableHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        disposableHandler = new DisposableHandler();
        ObserverWithBZ.setDefaultStyledProgressGenerator(new SampleStyledProgressGenerator());
        ObserverWithBZ.setDefaultErrorHandler(new SampleErrorHandler());
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }
}

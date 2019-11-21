package com.dhy.retrofitrxtest;

import android.app.Application;
import android.content.Context;

import com.dhy.retrofitrxutil.ObserverX;
import com.dhy.retrofitrxutil.SampleErrorHandler;
import com.dhy.retrofitrxutil.SampleStyledProgressGenerator;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ObserverX.setDefaultStyledProgressGenerator(new SampleStyledProgressGenerator());
        ObserverX.setDefaultErrorHandler(new SampleErrorHandler());
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }
}

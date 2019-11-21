package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.disposables.Disposable

/**
 * must call  DisposableHandler.onDestroy(this) in Activity.onDestroy
 * */
interface IDisposable {
    fun registerDisposable(context: Context, disposable: Disposable) {
        DisposableHandler.registerDisposable(context, disposable)
    }

    fun onComplete(context: Context, disposable: Disposable) {
        DisposableHandler.onComplete(context, disposable)
    }
}
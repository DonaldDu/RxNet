package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.disposables.Disposable

interface IDisposable {
    fun registerDisposable(context: Context, disposable: Disposable)
    fun onComplete(context: Context, disposable: Disposable)
}
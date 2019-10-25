package com.dhy.retrofitrxutil

import android.app.Application
import android.content.Context

import java.util.ArrayList
import java.util.HashMap

import io.reactivex.disposables.Disposable

class DisposableHandler : IDisposableHandler {
    private val map: MutableMap<Context, MutableList<Disposable>> = mutableMapOf()

    override fun registerDisposable(context: Context, disposable: Disposable) {
        if (context !is Application) {
            var list = map[context]
            if (list == null) {
                list = ArrayList()
                map[context] = list
            }
            list.add(disposable)
        }
    }

    override fun onComplete(context: Context, disposable: Disposable) {
        if (context !is Application) {
            val list = map[context]
            list?.remove(disposable)
        }
    }

    override fun onDestroy(context: Context) {
        val list = map.remove(context)
        if (list != null)
            for (disposable in list) {
                disposable.dispose()
            }
    }
}

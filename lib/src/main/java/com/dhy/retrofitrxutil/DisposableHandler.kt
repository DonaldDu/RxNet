package com.dhy.retrofitrxutil

import android.app.Application
import android.content.Context

import io.reactivex.disposables.Disposable
import java.util.*

object DisposableHandler : IDisposableHandler {
    private val requests: WeakHashMap<Context, MutableList<Disposable>> = WeakHashMap()

    override fun registerDisposable(context: Context, disposable: Disposable) {
        if (context !is Application) {
            var list = requests[context]
            if (list == null) {
                list = ArrayList()
                requests[context] = list
            }
            list.add(disposable)
        }
    }

    override fun onComplete(context: Context, disposable: Disposable) {
        if (context !is Application) {
            val list = requests[context]
            list?.remove(disposable)
        }
    }

    override fun onDestroy(context: Context) {
        val dialog = DelayDialogProgress.dialogs[context]
        if (dialog?.isShowing == true) dialog.dismiss()

        val list = requests.remove(context)
        if (list != null) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                iterator.next().dispose()
            }
        }
    }
}

package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.WeakReference
import java.util.*

/**
 * 所有请求进度框都延迟关闭，以实现多个连续请求中，进度框不闪烁。
 * */
class DelayDialogProgress(private val context: Activity, private val dialog: Dialog) : StyledProgress, LifecycleObserver {
    companion object {
        internal val dialogs: WeakHashMap<Context, DelayDialogProgress> = WeakHashMap()

        fun getInstace(context: Activity, observer: IObserverX, dialogCreater: (context: Activity) -> Dialog): DelayDialogProgress {
            val delayDialogProgress = dialogs[context] ?: DelayDialogProgress(context, dialogCreater(context))
            delayDialogProgress.canceler = WeakReference(observer)
            return delayDialogProgress
        }
    }

    private var canceler: WeakReference<IObserverX>? = null
    private var lifecycleOwner: LifecycleOwner? = context as? LifecycleOwner

    init {
        dialogs[context] = this
        dialog.addOnCancelListener {
            canceler?.get()?.cancel()
        }
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    override fun showProgress() {
        if (!dialog.isShowing) dialog.show()
        dialog.delayProgress.onShow()
    }

    override fun dismissProgress(delay: Boolean) {
        if (dialog.isShowing) {
            if (delay) dialog.delayProgress.onDismiss()
            else dialog.dismiss()
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        lifecycleOwner?.lifecycle?.removeObserver(this)
        dialogs.remove(context)
    }
}
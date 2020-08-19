package com.dhy.retrofitrxutil

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.dhy.xintent.isDebugable
import java.lang.ref.WeakReference

class MultListenerDialog(private val fragmentActivity: FragmentActivity) : Dialog(fragmentActivity), StyledProgress, LifecycleObserver {
    companion object {
        private val progresses: MutableMap<Context, MultListenerDialog> = mutableMapOf()

        fun getInstance(activity: FragmentActivity, observer: IObserverX? = null): MultListenerDialog {
            val progress = progresses[activity] ?: MultListenerDialog(activity)
            if (observer != null) progress.canceler = WeakReference(observer)
            return progress
        }
    }

    private val isDebug = fragmentActivity.isDebugable()
    private var canceler: WeakReference<IObserverX>? = null
    private val lifecycleOwner: LifecycleOwner = fragmentActivity

    private val delayProgress = DelayProgress()
    private val onDismissListeners: MutableSet<DialogInterface.OnDismissListener> = mutableSetOf()
    private val onCancelListeners: MutableSet<DialogInterface.OnCancelListener> = mutableSetOf()

    init {
        progresses[fragmentActivity] = this
        lifecycleOwner.lifecycle.addObserver(this)

        setContentView(R.layout.net_progress_dialog)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        setOnCancelListener {
            canceler?.get()?.cancel()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        lifecycleOwner.lifecycle.removeObserver(this)
        progresses.remove(fragmentActivity)
    }


    fun addOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.add(listener)
    }

    fun removeOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.remove(listener)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        if (listener != null) addOnDismissListener(listener)
        else if (isDebug) Toast.makeText(context, "use removeOnDismissListener instead", Toast.LENGTH_LONG).show()
    }

    fun addOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.add(listener)
    }

    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.remove(listener)
    }

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        if (listener != null) addOnCancelListener(listener)
        else if (isDebug) Toast.makeText(context, "use removeOnCancelListener instead", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        if (!isShowing) show()
        delayProgress.onShow()
    }

    override fun dismissProgress(delay: Boolean) {
        if (delay) delayProgress.onDismiss()
        else delayProgress.runnable.run()
    }

    private inner class DelayProgress {
        internal var count = 0
        private val decorView: View?
            get() {
                return window?.decorView
            }
        internal val runnable = Runnable {
            if (isShowing) dismiss()
        }

        fun onShow() {
            count++
            decorView?.removeCallbacks(runnable)
        }

        fun onDismiss() {
            if (count > 0) count--
            if (count == 0) {
                decorView?.postDelayed(runnable, 100)
            }
        }
    }
}

fun <E> Set<E>.iterator(action: (Iterator<E>) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) action(iterator)
}

fun FragmentActivity.showProgress(): MultListenerDialog {
    val dialog = MultListenerDialog.getInstance(this)
    dialog.showProgress()
    return dialog
}

fun FragmentActivity.dismissProgress(delay: Boolean = true) {
    MultListenerDialog.getInstance(this).dismissProgress(delay)
}
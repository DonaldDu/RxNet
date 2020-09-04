package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.DialogInterface.OnDismissListener
import android.view.View
import android.widget.Toast
import com.dhy.xintent.isDebugable

class MultListenerDialog(activity: Activity) : Dialog(activity), StyledProgress {
    companion object {
        private val progresses: MutableMap<Context, MultListenerDialog> = mutableMapOf()

        fun getInstance(activity: Activity): MultListenerDialog {
            WatchActivity.init(activity)
            return progresses[activity] ?: MultListenerDialog(activity)
        }

        internal fun onActivityDestroyed(activity: Activity) {
            val dialog = progresses.remove(activity)
            if (dialog?.isShowing == true) dialog.cancel()
        }
    }

    internal val requests: MutableMap<IObserverX, Any?> = mutableMapOf()
    private val isDebug = activity.isDebugable()
    private val delayProgress = DelayProgress()
    private val onDismissListeners: MutableSet<OnDismissListener> = mutableSetOf()
    private val onCancelListeners: MutableSet<OnCancelListener> = mutableSetOf()
    private val onDismissListener = OnDismissListener {
        onDismissListeners.iterator { it.onDismiss(this) }
    }
    private val onCancelListener = OnCancelListener {
        onCancelListeners.iterator { it.onCancel(this) }
    }

    init {
        progresses[activity] = this
        setContentView(R.layout.net_progress_dialog)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        addOnCancelListener { cancelAllRequests() }
        super.setOnCancelListener(onCancelListener)
        super.setOnDismissListener(onDismissListener)
    }

    private fun cancelAllRequests() {
        ArrayList(requests.keys).forEach {
            it.cancel()
        }
        requests.clear()
    }

    fun addOnDismissListener(listener: OnDismissListener) {
        onDismissListeners.add(listener)
    }

    /**
     * callback only one time
     * */
    fun addOnDismissListenerOnce(listener: OnDismissListener) {
        onDismissListeners.add(OnDismissListenerOnce(listener))
    }

    fun removeOnDismissListener(listener: OnDismissListener) {
        onDismissListeners.removeAll {
            it == listener || (it is OnDismissListenerOnce && it.listener == listener)
        }
    }

    override fun setOnDismissListener(listener: OnDismissListener?) {
        if (listener != null) addOnDismissListener(listener)
        else if (isDebug) Toast.makeText(context, "use removeOnDismissListener instead", Toast.LENGTH_LONG).show()
    }

    fun addOnCancelListener(listener: OnCancelListener) {
        onCancelListeners.add(listener)
    }

    /**
     * callback only one time, auto remove on dismiss
     * */
    fun addOnCancelListenerOnce(listener: OnCancelListener) {
        onCancelListeners.add(OnCancelListenerOnce(listener))
        addOnDismissListenerOnce { removeOnCancelListener(listener) }
    }

    fun removeOnCancelListener(listener: OnCancelListener) {
        onCancelListeners.removeAll {
            it == listener || (it is OnCancelListenerOnce && it.listener == listener)
        }
    }

    override fun setOnCancelListener(listener: OnCancelListener?) {
        if (listener != null) addOnCancelListener(listener)
        else if (isDebug) Toast.makeText(context, "use removeOnCancelListener instead", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        if (!isShowing) show()
        delayProgress.onShow()
    }

    override fun dismissProgress(delay: Boolean) {
        delayProgress.onDismiss(delay)
    }

    private inner class DelayProgress {
        private val decorView: View?
            get() {
                return window?.decorView
            }
        val runnable = Runnable {
            if (isShowing) dismiss()
        }

        fun onShow() {
            decorView?.removeCallbacks(runnable)
        }

        fun onDismiss(delay: Boolean) {
            if (delay) decorView?.postDelayed(runnable, 100)
            else {
                decorView?.removeCallbacks(runnable)
                runnable.run()
            }
        }
    }

    private fun <E> MutableSet<E>.iterator(action: (E) -> Unit) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val e = iterator.next()
            if (e is ListenerOnce) iterator.remove()
            action(e)
        }
    }

    private interface ListenerOnce
    private class OnDismissListenerOnce(val listener: OnDismissListener) : OnDismissListener, ListenerOnce {
        override fun onDismiss(dialog: DialogInterface?) {
            listener.onDismiss(dialog)
        }
    }

    private class OnCancelListenerOnce(val listener: OnCancelListener) : OnCancelListener, ListenerOnce {
        override fun onCancel(dialog: DialogInterface?) {
            listener.onCancel(dialog)
        }
    }
}

class StyledProgressHolder(private val dialog: MultListenerDialog, private val observer: IObserverX) : StyledProgress {
    private val requests = dialog.requests
    override fun showProgress() {
        requests[observer] = null//add in
        dialog.showProgress()
    }

    override fun dismissProgress(delay: Boolean) {
        requests.remove(observer)
        if (requests.isEmpty()) dialog.dismissProgress(delay)
    }
}

fun Activity.showProgress(): MultListenerDialog {
    val dialog = MultListenerDialog.getInstance(this)
    dialog.showProgress()
    return dialog
}

fun Activity.dismissProgress(delay: Boolean = true) {
    MultListenerDialog.getInstance(this).dismissProgress(delay)
}
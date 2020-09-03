package com.dhy.retrofitrxutil

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.DialogInterface.OnDismissListener
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
    private val onDismissListeners: MutableSet<OnDismissListener> = mutableSetOf()
    private val onCancelListeners: MutableSet<OnCancelListener> = mutableSetOf()
    private val onDismissListener = OnDismissListener {
        onDismissListeners.iterator { it.onDismiss(this) }
    }
    private val onCancelListener = OnCancelListener {
        onCancelListeners.iterator { it.onCancel(this) }
    }

    init {
        progresses[fragmentActivity] = this
        lifecycleOwner.lifecycle.addObserver(this)

        setContentView(R.layout.net_progress_dialog)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        addOnCancelListener { canceler?.get()?.cancel() }
        super.setOnCancelListener(onCancelListener)
        super.setOnDismissListener(onDismissListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        lifecycleOwner.lifecycle.removeObserver(this)
        progresses.remove(fragmentActivity)
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
        if (delay) delayProgress.onDismiss()
        else delayProgress.runnable.run()
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

        fun onDismiss() {
            decorView?.postDelayed(runnable, 100)
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

fun FragmentActivity.showProgress(): MultListenerDialog {
    val dialog = MultListenerDialog.getInstance(this)
    dialog.showProgress()
    return dialog
}

fun FragmentActivity.dismissProgress(delay: Boolean = true) {
    MultListenerDialog.getInstance(this).dismissProgress(delay)
}
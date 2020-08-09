package com.dhy.retrofitrxutil

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.WeakReference

class MultListenerDialog(private val fragmentActivity: FragmentActivity) : DialogFragment(), StyledProgress, LifecycleObserver {
    companion object {
        private val progresses: MutableMap<Context, MultListenerDialog> = mutableMapOf()

        fun getInstance(activity: FragmentActivity, observer: IObserverX? = null): MultListenerDialog {
            val progress = progresses[activity] ?: MultListenerDialog(activity)
            if (observer != null) progress.canceler = WeakReference(observer)
            return progress
        }
    }

    private var canceler: WeakReference<IObserverX>? = null

    private val lifecycleOwner: LifecycleOwner = fragmentActivity

    init {
        progresses[fragmentActivity] = this
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        lifecycleOwner.lifecycle.removeObserver(this)
        progresses.remove(fragmentActivity)
    }

    private val supportFragmentManager = fragmentActivity.supportFragmentManager
    private val delayProgress = DelayProgress()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireActivity()).setView(R.layout.net_progress_dialog).create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private val onDismissListeners: MutableSet<DialogInterface.OnDismissListener> = mutableSetOf()
    fun addOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.add(listener)
    }

    fun removeOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.remove(listener)
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListeners.iterator {
            it.next().onDismiss(dialog)
        }
        super.onDismiss(dialog)
        delayProgress.count = 0
    }

    private val onCancelListeners: MutableSet<DialogInterface.OnCancelListener> = mutableSetOf()
    fun addOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.add(listener)
    }

    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.remove(listener)
    }

    override fun onCancel(dialog: DialogInterface) {
        canceler?.get()?.cancel()
        onCancelListeners.iterator {
            it.next().onCancel(dialog)
        }
        super.onCancel(dialog)
    }

    override fun showProgress() {
        if (dialog?.isShowing != true) showNow(supportFragmentManager, javaClass.name)
        delayProgress.onShow()
    }

    override fun dismissProgress(delay: Boolean) {
        if (delay) delayProgress.onDismiss()
        else dismiss()
    }

    private inner class DelayProgress {
        internal var count = 0
        private val decorView: View?
            get() {
                return dialog?.window?.decorView
            }
        private val runnable = Runnable {
            if (dialog?.isShowing == true) dismiss()
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
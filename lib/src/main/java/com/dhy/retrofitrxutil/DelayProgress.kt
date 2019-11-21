package com.dhy.retrofitrxutil

import android.app.Dialog
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context

val Dialog.delayProgress: DelayProgress
    get() {
        val v = window!!.decorView
        val key = R.id.TAG_KEY_DELAY_PROGRESS
        var data = v.getTag(key) as DelayProgress?
        return if (data != null) data
        else {
            data = DelayProgress(context, this)
            v.setTag(key, data)
            data
        }
    }


class DelayProgress(private val context: Context, private val dialog: Dialog) : LifecycleObserver {
    private val decorView = dialog.window!!.decorView
    private val runnable = Runnable {
        dialog.dismiss()
    }
    private var count = 0

    private var addObserver = false

    init {
        addObserver()
        dialog.setOnDismissListener {
            count = 0
            decorView.removeCallbacks(runnable)
        }
    }

    fun onShow() {
        count++
        decorView.removeCallbacks(runnable)
    }

    fun onDismiss() {
        if (count > 0) count--
        if (count == 0) {
            addObserver()
            decorView.postDelayed(runnable, 100)
        }
    }

    private fun addObserver() {
        if (!addObserver && context is LifecycleOwner) {
            addObserver = true
            context.lifecycle.addObserver(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        if (dialog.isShowing) {
            decorView.removeCallbacks(runnable)
            dialog.dismiss()
        }
    }
}
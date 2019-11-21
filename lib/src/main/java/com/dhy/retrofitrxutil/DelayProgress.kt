package com.dhy.retrofitrxutil

import android.app.Dialog
import android.arch.lifecycle.LifecycleObserver

val Dialog.delayProgress: DelayProgress
    get() {
        val v = window!!.decorView
        val key = R.id.TAG_KEY_DELAY_PROGRESS
        var data = v.getTag(key) as DelayProgress?
        return if (data != null) data
        else {
            data = DelayProgress(this)
            v.setTag(key, data)
            data
        }
    }

class DelayProgress(private val dialog: Dialog) : LifecycleObserver {
    private val decorView = dialog.window!!.decorView
    private val runnable = Runnable {
        if (dialog.isShowing) dialog.dismiss()
    }
    private var count = 0

    init {
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
            decorView.postDelayed(runnable, 100)
        }
    }
}
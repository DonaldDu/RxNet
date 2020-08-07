package com.dhy.retrofitrxutil

import android.app.Dialog

val Dialog.delayProgress: DelayProgress
    get() {
        return getTag(R.id.TAG_KEY_DELAY_PROGRESS) { DelayProgress(this) }
    }

class DelayProgress(private val dialog: Dialog) {
    private val decorView = dialog.window!!.decorView
    private val runnable = Runnable {
        if (dialog.isShowing) dialog.dismiss()
    }
    private var count = 0

    init {
        dialog.addOnDismissListener {
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
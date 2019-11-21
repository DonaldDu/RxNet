package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import java.util.*

/**
 * 所有请求进度框都延迟关闭，以实现多个连续请求中，进度框不闪烁。
 * */
class DelayDialogProgress(private val context: Activity, private val cancelListener: DialogInterface.OnCancelListener, private val dialogCreater: (context: Activity) -> Dialog) : StyledProgress {
    companion object {
        internal val dialogs: WeakHashMap<Context, Dialog> = WeakHashMap()
    }

    override fun showProgress() {
        val dialog = getDialog(context, true)
        if (dialog != null) {
            if (!dialog.isShowing) dialog.show()
            dialog.setOnCancelListener(cancelListener)
            dialog.delayProgress.onShow()
        }
    }

    override fun dismissProgress() {
        val dialog = getDialog(context, false)
        if (dialog != null && dialog.isShowing) {
            dialog.delayProgress.onDismiss()
        }
    }

    private fun getDialog(context: Activity, show: Boolean): Dialog? {
        var dialog = dialogs[context]
        return dialog ?: if (show) {
            dialog = dialogCreater(context)
            dialogs[context] = dialog
            dialog
        } else null
    }
}
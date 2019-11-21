package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import java.util.*

class DialogProgress(private val context: Activity, private val cancelListener: DialogInterface.OnCancelListener, private val dialogCreater: (context: Activity) -> Dialog) : StyledProgress {
    companion object {
        private val dialogs: WeakHashMap<Context, Dialog> = WeakHashMap()
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
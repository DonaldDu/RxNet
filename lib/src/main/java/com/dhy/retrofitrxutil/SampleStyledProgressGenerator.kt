package com.dhy.retrofitrxutil

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.util.*

class SampleStyledProgressGenerator : StyledProgressGenerator {
    private val map: WeakHashMap<Context, Dialog> = WeakHashMap()
    override fun generate(observer: IObserverX): StyledProgress {
        return object : StyledProgress {
            val context = observer.context
            override fun showProgress() {
                show(true)
            }

            override fun dismissProgress() {
                show(false)
            }

            var toast: Toast? = null
            @SuppressLint("ShowToast")
            private fun show(show: Boolean) {
                if (context is Activity) {
                    if (context.isFinishing) return//fixme: BE CAREFUL OF THIS

                    val dialog = getDialog(context)
                    if (show) {
                        if (!dialog.isShowing) {
                            dialog.show()
                            Log.d("TAG", "dialog.show")
                        }
                        dialog.setOnCancelListener { observer.cancel() }
                        dialog.delayProgress.onShow()
                    } else if (dialog.isShowing) {
                        dialog.delayProgress.onDismiss()
                    }
                } else {
                    if (toast == null) {
                        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
                    }
                    toast!!.setText("showProgress: $show")
                    toast!!.show()
                }
            }
        }
    }

    private fun getDialog(context: Context): Dialog {
        var dialog = map[context]
        return if (dialog != null) dialog
        else {
            @Suppress("DEPRECATION")
            dialog = ProgressDialog(context)
            map[context] = dialog
            dialog
        }
    }
}
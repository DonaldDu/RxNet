package com.dhy.retrofitrxutil

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.widget.Toast

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress {
        return object : StyledProgress {
            val context = observer.context
            override fun showProgress() {
                show(true)
            }

            override fun dismissProgress() {
                show(false)
            }

            var dialog: Dialog? = null
            var toast: Toast? = null
            @SuppressLint("ShowToast")
            private fun show(show: Boolean) {
                if (context is Activity) {
                    if (dialog == null) dialog = ProgressDialog(context)
                    if (show) {
                        if (!dialog!!.isShowing) dialog!!.show()
                        dialog!!.setOnCancelListener { observer.cancel() }
                    } else if (dialog!!.isShowing) {
                        dialog!!.dismiss()
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
}
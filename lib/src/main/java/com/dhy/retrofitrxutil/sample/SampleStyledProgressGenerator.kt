package com.dhy.retrofitrxutil.sample

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import com.dhy.retrofitrxutil.DelayDialogProgress
import com.dhy.retrofitrxutil.IObserverX
import com.dhy.retrofitrxutil.StyledProgress
import com.dhy.retrofitrxutil.StyledProgressGenerator

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        return if (observer.context is Activity) {
            DelayDialogProgress(observer.context as Activity, DialogInterface.OnCancelListener { observer.cancel() }) {
                ProgressDialog(it)
            }
        } else null
    }
}
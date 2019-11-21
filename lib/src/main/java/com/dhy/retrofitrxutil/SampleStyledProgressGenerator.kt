package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        return if (observer.context is Activity) {
            DialogProgress(observer.context as Activity, DialogInterface.OnCancelListener { observer.cancel() }) {
                ProgressDialog(it)
            }
        } else null
    }
}
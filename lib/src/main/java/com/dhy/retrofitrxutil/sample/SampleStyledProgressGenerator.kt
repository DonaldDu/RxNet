package com.dhy.retrofitrxutil.sample

import android.app.Activity
import android.app.ProgressDialog
import com.dhy.retrofitrxutil.DelayDialogProgress
import com.dhy.retrofitrxutil.IObserverX
import com.dhy.retrofitrxutil.StyledProgress
import com.dhy.retrofitrxutil.StyledProgressGenerator

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        val context = observer.context
        return if (context is Activity) {
            DelayDialogProgress.getInstace(context, observer) {
                ProgressDialog(it)
            }
        } else null
    }
}
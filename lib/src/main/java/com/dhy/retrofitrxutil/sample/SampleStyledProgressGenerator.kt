package com.dhy.retrofitrxutil.sample

import android.app.Activity
import com.dhy.retrofitrxutil.*

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        val context = observer.context
        return if (context is Activity) {
            val dialog = MultListenerDialog.getInstance(context)
            StyledProgressHolder(dialog, observer)
        } else null
    }
}
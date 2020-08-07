package com.dhy.retrofitrxutil.sample

import androidx.fragment.app.FragmentActivity
import com.dhy.retrofitrxutil.IObserverX
import com.dhy.retrofitrxutil.MultListenerDialog
import com.dhy.retrofitrxutil.StyledProgress
import com.dhy.retrofitrxutil.StyledProgressGenerator

class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        val context = observer.context
        return if (context is FragmentActivity) {
            MultListenerDialog.getInstance(context, observer)
        } else null
    }
}
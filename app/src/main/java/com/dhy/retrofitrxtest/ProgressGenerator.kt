package com.dhy.retrofitrxtest

import androidx.fragment.app.FragmentActivity
import com.dhy.retrofitrxutil.IObserverX
import com.dhy.retrofitrxutil.StyledProgress
import com.dhy.retrofitrxutil.StyledProgressGenerator

class ProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        val context = observer.context
        return if (context is FragmentActivity) {
            MultListenerDialogFragment(context)
        } else null
    }
}
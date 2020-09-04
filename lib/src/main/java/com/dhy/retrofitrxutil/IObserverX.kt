package com.dhy.retrofitrxutil

import android.content.Context

interface IObserverX {
    fun dismissProgress(delay: Boolean = true)
    val context: Context
    fun cancel()
    fun isCanceled(): Boolean
}
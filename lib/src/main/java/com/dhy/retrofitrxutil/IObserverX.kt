package com.dhy.retrofitrxutil

import android.content.Context

interface IObserverX {
    fun dismissProgress()
    val context: Context
    fun cancel()
}
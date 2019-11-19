package com.dhy.retrofitrxutil

import android.content.Context

interface IDisposableHandler : IDisposable {
    fun onDestroy(context: Context)
}
package com.dhy.retrofitrxtest

import android.support.v7.app.AppCompatActivity
import com.dhy.retrofitrxutil.DisposableHandler
import com.dhy.retrofitrxutil.IDisposable

abstract class BaseActivity : AppCompatActivity(), IDisposable {
    override fun onDestroy() {
        super.onDestroy()
        DisposableHandler.onDestroy(this)
    }
}
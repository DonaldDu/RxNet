package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable

class ObserverXBuilder<T>(private val context: Context, private val observable: Observable<T>) {
    private var failed: ((IResponseStatus) -> Boolean)? = null
    private var progress: (((StyledProgress?) -> StyledProgress?))? = null
    private var successOnly: Boolean = true
    private var autoDismiss: Boolean = true
    private var response: (T) -> Unit = {}

    fun failed(handler: ((IResponseStatus) -> Boolean)?): ObserverXBuilder<T> {
        this.failed = handler
        return this
    }

    fun progress(progress: (((StyledProgress?) -> StyledProgress?))?): ObserverXBuilder<T> {
        this.progress = progress
        return this
    }

    fun successOnly(successOnly: Boolean): ObserverXBuilder<T> {
        this.successOnly = successOnly
        return this
    }

    fun autoDismiss(autoDismiss: Boolean): ObserverXBuilder<T> {
        this.autoDismiss = autoDismiss
        return this
    }

    fun response(response: (T) -> Unit): ObserverXBuilder<T> {
        this.response = response
        return this
    }

    fun build() {
        observable.subscribe(object : ObserverX<T>(context, successOnly, autoDismiss) {
            override fun onResponse(t: T) {
                response(t)
            }

            override fun onFailed(status: IResponseStatus) {
                if (failed != null) {
                    val hanle = failed!!(status)
                    if (!hanle) super.onFailed(status)
                } else super.onFailed(status)
            }

            override fun getStyledProgress(): StyledProgress? {
                if (progress != null) return progress!!(super.getStyledProgress())
                return super.getStyledProgress()
            }
        })
    }
}

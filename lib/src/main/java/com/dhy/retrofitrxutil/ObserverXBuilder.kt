package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ObserverXBuilder<T>(private val context: Context, private val observable: Observable<T>) {
    private var failed: ((IResponseStatus) -> Boolean)? = null
    private var progress: (((StyledProgress?) -> StyledProgress?))? = null
    private var successOnly: Boolean = true
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

    /**
     * set response clouser and build to start request
     * */
    fun response(response: (T) -> Unit) {
        this.response = response
        build()
    }

    fun build() {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ObserverX<T>(context, successOnly) {
                    override fun onResponse(response: T) {
                        response(response)
                    }

                    override fun onFailed(status: IResponseStatus) {
                        if (failed != null) {
                            val hanle = failed!!(status)
                            if (!hanle) super.onFailed(status)
                        } else super.onFailed(status)
                    }

                    override fun onError(e: Throwable) {
                        if (failed != null) {
                            val error = errorHandler.parseError(e)
                            val status = object : IResponseStatus {
                                override fun getCode() = error.code
                                override fun httpCode() = error.httpCode()
                                override fun getMessage() = error.message
                                override fun isSuccess() = false
                            }
                            val hanle = failed!!(status)
                            if (!hanle) super.onError(ThrowableBZ(status))
                        } else super.onError(e)
                    }

                    override fun getStyledProgress(): StyledProgress? {
                        if (progress != null) return progress!!(super.getStyledProgress())
                        return super.getStyledProgress()
                    }
                })
    }
}

package com.dhy.retrofitrxutil

import android.app.Activity
import android.content.Context
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class ObserverX<T>(override val context: Context, private val successOnly: Boolean = true, private val autoDismiss: Boolean = true) : Observer<T>, IObserverX {
    private var disposable: Disposable? = null
    private val progress: StyledProgress by lazy {
        getStyledProgress() ?: StyledProgressOfNone.instance
    }

    /**
     * Override this for custom progress
     *
     * @return null for StyledProgressOfNone
     */
    protected open fun getStyledProgress(): StyledProgress? {
        return defaultStyledProgressGenerator?.generate(this)
    }

    override fun onSubscribe(disposable: Disposable) {
        this.disposable = disposable
        progress.showProgress()
        if (context is IDisposable) {
            (context as IDisposable).registerDisposable(context, disposable)
        }
    }

    override fun onNext(t: T) {
        if (successOnly && t is IResponseStatus) {
            val status = t as IResponseStatus
            if (status.isSuccess) {
                onResponse(t)
            } else {
                onFailed(status)
            }
        } else onResponse(t)
    }

    protected open fun onFailed(status: IResponseStatus) {
        onError(ThrowableBZ(status))
    }

    /**
     * Override this for custom ErrorHandler
     */
    protected open val errorHandler: IErrorHandler
        get() = defaultErrorHandler!!

    override fun onError(e: Throwable) {
        errorHandler.onError(this, e)
    }

    override fun onComplete() {
        cancel()
        if (autoDismiss || forceDismissProgress) dismissProgress()
    }

    fun showProgress() {
        if (!isFinishing()) progress.showProgress()
    }

    private fun isFinishing(): Boolean {
        return context is Activity && (context as Activity).isFinishing
    }

    override fun dismissProgress() {
        progress.dismissProgress()
    }

    protected abstract fun onResponse(response: T)
    override fun cancel() {
        if (disposable != null) {
            disposable!!.dispose()
            if (context is IDisposable) {
                (context as IDisposable).onComplete(context, disposable!!)
            }
        }
    }

    companion object {
        private var defaultErrorHandler: IErrorHandler? = null
        private var defaultStyledProgressGenerator: StyledProgressGenerator? = null
        @JvmStatic
        fun setDefaultErrorHandler(handler: IErrorHandler?) {
            defaultErrorHandler = handler
        }

        @JvmStatic
        fun setDefaultStyledProgressGenerator(generator: StyledProgressGenerator?) {
            defaultStyledProgressGenerator = generator
        }

        var forceDismissProgress = true
    }
}
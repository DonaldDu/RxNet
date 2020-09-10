package com.dhy.retrofitrxutil

import android.app.Activity
import android.content.Context
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

abstract class ObserverX<T>(final override val context: Context, private val successOnly: Boolean = true) : Observer<T>, IObserverX {
    private var disposable: Disposable? = null
    private val progress by lazy { getStyledProgress() }

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
        showProgress()
        if (context is Activity) requests[this] = context
    }

    internal fun onDestroy() {
        cancel()
    }

    private fun removeObserver() {
        if (context is Activity) requests.remove(this)
    }

    override fun onNext(t: T) {
        dismissProgress(true)
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

    final override fun onError(e: Throwable) {
        dismissProgress(false)
        onErrorResponse(e)
    }

    open fun onErrorResponse(e: Throwable) {
        errorHandler.onError(this, e)
    }

    override fun onComplete() {
        disposable = null
    }

    private fun showProgress() {
        if (!isFinishing()) progress?.showProgress()
    }

    private fun isFinishing(): Boolean {
        return context is Activity && context.isFinishing
    }

    override fun dismissProgress(delay: Boolean) {
        progress?.dismissProgress(delay)
        removeObserver()
    }

    protected abstract fun onResponse(response: T)
    override fun cancel() {
        disposable?.dispose()
        dismissProgress(false)
    }

    override fun isCanceled(): Boolean {
        return disposable == null || disposable?.isDisposed == true
    }

    companion object {
        private val requests: MutableMap<ObserverX<*>, Activity> = mutableMapOf()
        internal fun onActivityDestroyed(activity: Activity) {
            ArrayList(requests.keys).forEach {
                val v = requests[it]
                if (v == activity) it.onDestroy()
            }
        }

        private var defaultErrorHandler: IErrorHandler? = null
        private var defaultStyledProgressGenerator: StyledProgressGenerator? = null

        @JvmStatic
        @Deprecated("use setErrorHandler", replaceWith = ReplaceWith("ObserverX.setErrorHandler(handler)"))
        fun setDefaultErrorHandler(handler: IErrorHandler?) {
            defaultErrorHandler = handler
        }

        @JvmStatic
        fun setErrorHandler(handler: IErrorHandler?) {
            defaultErrorHandler = handler
        }

        @JvmStatic
        @Deprecated("use setProgressGenerator", replaceWith = ReplaceWith("ObserverX.setProgressGenerator(generator)"))
        fun setDefaultStyledProgressGenerator(generator: StyledProgressGenerator?) {
            defaultStyledProgressGenerator = generator
        }

        @JvmStatic
        fun setProgressGenerator(generator: StyledProgressGenerator?) {
            defaultStyledProgressGenerator = generator
        }
    }
}
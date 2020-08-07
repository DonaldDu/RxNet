package com.dhy.retrofitrxutil

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

abstract class ObserverX<T>(final override val context: Context, private val successOnly: Boolean = true) : Observer<T>, IObserverX, LifecycleObserver {
    private var disposable: Disposable? = null
    private var lifecycleOwner: LifecycleOwner? = context as? LifecycleOwner
    private val progress: StyledProgress? by lazy {
        getStyledProgress()
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
        progress?.showProgress()
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        dismissProgress(false)
        removeObserver()
        cancel()
    }

    private fun removeObserver() {
        lifecycleOwner?.lifecycle?.removeObserver(this)
    }

    override fun onNext(t: T) {
        removeObserver()
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
        removeObserver()
        errorHandler.onError(this, e)
    }

    override fun onComplete() {
        cancel()
        dismissProgress()
    }

    fun showProgress() {
        if (!isFinishing()) progress?.showProgress()
    }

    private fun isFinishing(): Boolean {
        return context is Activity && context.isFinishing
    }

    override fun dismissProgress(delay: Boolean) {
        progress?.dismissProgress(delay)
    }

    protected abstract fun onResponse(response: T)
    override fun cancel() {
        disposable?.dispose()
    }

    companion object {
        private var defaultErrorHandler: IErrorHandler? = null
        private var defaultStyledProgressGenerator: StyledProgressGenerator? = null

        @JvmStatic
        @Deprecated("use setErrorHandler", replaceWith = ReplaceWith("setErrorHandler(handler)"))
        fun setDefaultErrorHandler(handler: IErrorHandler?) {
            defaultErrorHandler = handler
        }

        @JvmStatic
        fun setErrorHandler(handler: IErrorHandler?) {
            defaultErrorHandler = handler
        }

        @JvmStatic
        @Deprecated("use setProgressGenerator", replaceWith = ReplaceWith("setProgressGenerator(generator)"))
        fun setDefaultStyledProgressGenerator(generator: StyledProgressGenerator?) {
            defaultStyledProgressGenerator = generator
        }

        @JvmStatic
        fun setProgressGenerator(generator: StyledProgressGenerator?) {
            defaultStyledProgressGenerator = generator
        }
    }
}
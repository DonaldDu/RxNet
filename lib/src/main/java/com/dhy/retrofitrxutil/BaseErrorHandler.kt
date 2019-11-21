package com.dhy.retrofitrxutil

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.widget.Toast
import retrofit2.HttpException

abstract class BaseErrorHandler : IErrorHandler {

    override fun onError(observer: IObserverX, e: Throwable) {
        observer.dismissProgress()
        val error = parseError(e)
        val context = observer.context
        if (context is Activity) {
            if (!context.isFinishing) onActivityError(context, error)
        } else {
            onBackgroundError(context, error, e)
        }
    }

    override fun onActivityError(activity: Activity, error: IError) {
        val dialog = showDialog(activity, error.message)
        if (dialog != null && isAuthorizeFailed(activity, error.code)) {
            dialog.setOnDismissListener { onLogout(activity) }
        }
    }

    override fun onBackgroundError(context: Context, error: IError, e: Throwable) {
        if (isDebug()) {
            if (Looper.myLooper() != null) {
                val msg = error.message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }
    }

    override fun parseError(e: Throwable): IError {
        val code: Int
        val message: String
        when (e) {
            is ThrowableBZ -> return e.status
            is HttpException -> {
                code = e.code()
                message = "HTTP $code"
            }
            else -> {
                code = -1
                var msg: String? = e.message
                if (msg == null || msg.isEmpty()) {
                    msg = e.javaClass.name
                }
                message = msg!!
            }
        }
        return Error(code, message)
    }
}

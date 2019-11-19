package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Dialog
import android.content.Context

interface IErrorHandler {
    fun onError(observer: IObserverX, e: Throwable)
    fun showDialog(context: Context, msg: String): Dialog?
    fun parseError(e: Throwable): IError
    /**
     * if failed then logout. Should not be LoginActivity
     */
    fun isAuthorizeFailed(activity: Activity, errorCode: Int): Boolean

    fun onLogout(context: Context)
    fun onActivityError(activity: Activity, error: IError)
    fun onBackgroundError(context: Context, error: IError, e: Throwable)
    fun isDebug(): Boolean
}
package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable

/**
 * auto request in io, response in main thread
 * */
fun <T : Any> Observable<T>.subscribeX(context: Context, response: (T) -> Unit) {
    ObserverXBuilder(context, this)
            .response(response)
}

@Deprecated(message = "autoDismiss is Deprecated. Use DelayDialogProgress for global setting.",
        replaceWith = ReplaceWith("this.subscribeX(context,response)"))
fun <T : Any> Observable<T>.subscribeX(context: Context, autoDismiss: Boolean, response: (T) -> Unit) {
    ObserverXBuilder(context, this)
            .response(response)
}

fun <T : Any> Observable<T>.subscribeXBuilder(context: Context): ObserverXBuilder<T> {
    return ObserverXBuilder(context, this)
}

/**
 * start request immediately, send response back beyond atLeastMillis time
 * */
fun <T : Any> Observable<T>.delayResponse(atLeastMillis: Long = 1500): Observable<T> {
    val start = System.currentTimeMillis()
    return map {
        val delay = atLeastMillis - (System.currentTimeMillis() - start)
        if (delay > 0) Thread.sleep(delay)
        it
    }
}
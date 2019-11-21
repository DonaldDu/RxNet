package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable

fun <T : Any> Observable<T>.subscribeX(context: Context, response: (T) -> Unit) {
    ObserverXBuilder(context, this)
            .response(response)
}

@Deprecated(message = "autoDismiss is Deprecated")
fun <T : Any> Observable<T>.subscribeX(context: Context, autoDismiss: Boolean, response: (T) -> Unit) {
    ObserverXBuilder(context, this)
            .response(response)
}

fun <T : Any> Observable<T>.subscribeXBuilder(context: Context): ObserverXBuilder<T> {
    return ObserverXBuilder(context, this)
}
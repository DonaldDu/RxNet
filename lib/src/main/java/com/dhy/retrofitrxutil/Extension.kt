package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable

fun <T : Any> Observable<T>.subscribeX(context: Context, autoDismiss: Boolean = true, response: (T) -> Unit) {
    ObserverXBuilder(context, this)
            .autoDismiss(autoDismiss)
            .response(response)
}

fun <T : Any> Observable<T>.subscribeXBuilder(context: Context): ObserverXBuilder<T> {
    return ObserverXBuilder(context, this)
}
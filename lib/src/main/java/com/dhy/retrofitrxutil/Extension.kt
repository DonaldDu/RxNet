package com.dhy.retrofitrxutil

import android.content.Context
import io.reactivex.Observable

fun <T : Any> Observable<T>.subscribeX(context: Context,
                                       successOnly: Boolean = true,
                                       autoDismiss: Boolean = true,
                                       failed: ((IResponseStatus) -> Boolean)? = null,
                                       progress: (((StyledProgress?) -> StyledProgress?))? = null,
                                       response: (T) -> Unit
) {
    subscribe(object : ObserverX<T>(context, successOnly, autoDismiss) {
        override fun onResponse(t: T) {
            response(t)
        }

        override fun onFailed(status: IResponseStatus) {
            if (failed != null) {
                val hanle = failed(status)
                if (!hanle) super.onFailed(status)
            } else super.onFailed(status)
        }

        override fun getStyledProgress(): StyledProgress? {
            if (progress != null) return progress(super.getStyledProgress())
            return super.getStyledProgress()
        }
    })
}
package com.dhy.retrofitrxutil

import android.app.Dialog
import android.content.DialogInterface


fun Dialog.addOnCancelListener(listener: () -> Unit) {
    addOnCancelListener(DialogInterface.OnCancelListener { listener.invoke() })
}

fun Dialog.addOnCancelListener(listener: DialogInterface.OnCancelListener) {
    onCancelListeners.add(listener)
}

fun Dialog.removeOnCancelListener(listener: DialogInterface.OnCancelListener) {
    onCancelListeners.remove(listener)
}

val Dialog.onCancelListeners: MutableSet<DialogInterface.OnCancelListener>
    get() {
        val data: MultOnCancelListener = getTag(R.id.ADD_ON_CANCEL_LISTENER) {
            val l = MultOnCancelListener()
            setOnCancelListener(l)
            l
        }
        return data.listeners
    }

class MultOnCancelListener : DialogInterface.OnCancelListener {
    internal val listeners: MutableSet<DialogInterface.OnCancelListener> = mutableSetOf()
    override fun onCancel(dialog: DialogInterface?) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onCancel(dialog)
        }
    }
}
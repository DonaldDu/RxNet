package com.dhy.retrofitrxutil

import android.app.Dialog
import android.content.DialogInterface

fun Dialog.addOnDismissListener(listener: () -> Unit) {
    addOnDismissListener(DialogInterface.OnDismissListener { listener.invoke() })
}

fun Dialog.addOnDismissListener(listener: DialogInterface.OnDismissListener) {
    onDismissListeners.add(listener)
}

fun Dialog.removeOnDismissListener(listener: DialogInterface.OnDismissListener) {
    onDismissListeners.remove(listener)
}

val Dialog.onDismissListeners: MutableSet<DialogInterface.OnDismissListener>
    get() {
        val data: MultOnDismissListener = getTag(R.id.ADD_ON_DISMISS_LISTENER) {
            val l = MultOnDismissListener()
            setOnDismissListener(l)
            l
        }
        return data.listeners
    }

class MultOnDismissListener : DialogInterface.OnDismissListener {
    internal val listeners: MutableSet<DialogInterface.OnDismissListener> = mutableSetOf()
    override fun onDismiss(dialog: DialogInterface?) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onDismiss(dialog)
        }
    }
}
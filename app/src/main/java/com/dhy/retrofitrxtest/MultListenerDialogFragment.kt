package com.dhy.retrofitrxtest

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.dhy.retrofitrxutil.StyledProgress

class MultListenerDialogFragment(fragmentActivity: FragmentActivity) : DialogFragment(), StyledProgress {
    private val supportFragmentManager = fragmentActivity.supportFragmentManager
    private val dialogTag = javaClass.name
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireActivity()).setView(R.layout.net_progress_dialog).create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private val onDismissListeners: MutableSet<DialogInterface.OnDismissListener> = mutableSetOf()
    fun addOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.add(listener)
    }

    fun removeOnDismissListener(listener: DialogInterface.OnDismissListener) {
        onDismissListeners.remove(listener)
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListeners.iterator {
            it.next().onDismiss(dialog)
        }
        super.onDismiss(dialog)
    }

    private val onCancelListeners: MutableSet<DialogInterface.OnCancelListener> = mutableSetOf()
    fun addOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.add(listener)
    }

    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener) {
        onCancelListeners.remove(listener)
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancelListeners.iterator {
            it.next().onCancel(dialog)
        }
        super.onCancel(dialog)
    }

    override fun showProgress() {
        val shown = supportFragmentManager.findFragmentByTag(dialogTag) != null
        if (!shown) showNow(supportFragmentManager, dialogTag)
    }

    override fun dismissProgress(delay: Boolean) {
        dismiss()
    }
}

fun <E> Set<E>.iterator(action: (Iterator<E>) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) action(iterator)
}
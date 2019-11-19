package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Toast

class SampleErrorHandler : BaseErrorHandler() {
    override fun showDialog(context: Context, msg: String): Dialog? {
        return AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", null).show()
    }

    override fun isAuthorizeFailed(activity: Activity, errorCode: Int): Boolean {
        return errorCode == 9001
    }

    override fun onLogout(context: Context) {
        val msg = "onLogout"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        Log.i(TAG, msg)
    }

    override fun isDebug(): Boolean = true

    companion object {
        private val TAG = IErrorHandler::class.java.simpleName
    }
}
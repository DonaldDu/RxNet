package com.dhy.retrofitrxutil

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

internal object WatchActivity {
    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityDestroyed(activity: Activity) {
            MultListenerDialog.onActivityDestroyed(activity)
            ObserverX.onActivityDestroyed(activity)
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity?) {}
        override fun onActivityResumed(activity: Activity?) {}
        override fun onActivityPaused(activity: Activity?) {}
        override fun onActivityStopped(activity: Activity?) {}
        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    }

    private var registered = false
    fun init(context: Context) {
        if (registered) return
        registered = true
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}

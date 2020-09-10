package com.dhy.retrofitrxtest

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicBoolean


class SimpleIdlingResource : IdlingResource {
    @Volatile
    private var mCallback: ResourceCallback? = null

    // Idleness is controlled with this boolean.
    private val mIsIdleNow: AtomicBoolean = AtomicBoolean(true)

    //用来标识 IdlingResource 名称
    override fun getName(): String {
        return this.javaClass.name
    }

    //当前 IdlingResource 是否空闲
    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    //注册一个空闲状态变换的ResourceCallback回调
    override fun registerIdleTransitionCallback(callback: ResourceCallback) {
        mCallback = callback
    }

    /**
     * 设置idle的状态，如果成功请求到了数据将isIdleNow设为true
     */
    fun setIdleState(isIdleNow: Boolean) {
        mIsIdleNow.set(isIdleNow)
        if (isIdleNow && mCallback != null) {
            mCallback!!.onTransitionToIdle()
        }
    }
}
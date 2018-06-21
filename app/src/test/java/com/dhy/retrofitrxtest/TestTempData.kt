package com.dhy.retrofitrxtest

import com.dhy.retrofitrxtest.data.UserPermission
import com.dhy.retrofitrxtest.util.RetrofitUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import junit.framework.TestCase
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TestTempData : TestCase() {
    private fun <T> T.tag(tag: Any): T {
        return RetrofitUtil.tag(this, tag)
    }

    private lateinit var api: TestingApi

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://www.wwvas.cn:9107/vasms-web/")
                .build()
        api = RetrofitUtil.create(retrofit, TestingApi::class.java)
    }

    @Synchronized
    fun testSimple() {
        api.fetchUserPermissions("512ef3e88ceb590c15540c34e3b34773")
                .subscribe(object : Observer<ResponsePacket<Array<UserPermission>>> {
                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(response: ResponsePacket<Array<UserPermission>>) {
                        println("onNext: " + response.message)
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }

                    override fun onComplete() {
                        println("onComplete")
                    }
                })
    }

    fun testTagx() {
        tagx(true)
    }

    private fun tagx(successOnly: Boolean) {
        api.tag(successOnly).fetchUserPermissions("512ef3e88ceb590c15540c34e3b34773+")
                .subscribe(object : Observer<ResponsePacket<Array<UserPermission>>> {
                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(response: ResponsePacket<Array<UserPermission>>) {
                        println("onNext: " + response.message)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: " + e.message)
                    }

                    override fun onComplete() {
                        println("onComplete")
                    }
                })
    }

}

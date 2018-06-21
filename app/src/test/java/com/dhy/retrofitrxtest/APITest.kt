package com.dhy.retrofitrxtest

import com.dhy.retrofitrxtest.util.RetrofitUtil
import com.google.gson.Gson
import junit.framework.TestCase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class APITest : TestCase() {
    private val gson = Gson()
    private lateinit var api: API
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        val client = OkHttpClient.Builder().addInterceptor(MockDataApiInterceptor()).build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://www.demo.com/vasms-web/")
                .build()
        api = RetrofitUtil.create(retrofit, API::class.java)
    }

    fun testParse() {
        var packet = gson.fromJson(MockDataApiInterceptor.allOk, ResponsePacket::class.java)
        println(gson.toJson(packet))
        packet = gson.fromJson(MockDataApiInterceptor.bzError, ResponsePacket::class.java)
        println(gson.toJson(packet))
    }

    fun testAllOK() {
        api.simple().subscribe({
            println(gson.toJson(it))
        }, {
            it.printStackTrace()
        })
    }

    fun testNetError() {
        api.netError().subscribe({
            println(gson.toJson(it))
        }, {
            it.printStackTrace()
        })
    }

    fun testBzError() {
        api.bzError().subscribe({
            println(gson.toJson(it))
        }, {
            it.printStackTrace()
        })
    }
}

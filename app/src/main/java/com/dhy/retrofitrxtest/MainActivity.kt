package com.dhy.retrofitrxtest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dhy.retrofitrxutil.ObserverWithBZ
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : BaseActivity(), View.OnClickListener {
    lateinit var context: Context
    private lateinit var api: API
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        initApi()
        val root = findViewById<ViewGroup>(R.id.root)
        for (i in 0 until root.childCount) {
            root.getChildAt(i).setOnClickListener(this)
        }
    }

    private fun initApi() {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(MockDataApiInterceptor()).build()
        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://www.demo.com/")
                .build()
        api = retrofit.create(API::class.java)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonOK ->
                api.simple()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ObserverWithBZ<ResponsePacket<String>>(context) {
                            override fun onResponse(response: ResponsePacket<String>) {
                                Toast.makeText(context, "response:" + response.message, Toast.LENGTH_SHORT).show()
                            }
                        })

            R.id.buttonNetError ->
                api.netError()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ObserverWithBZ<ResponsePacket<String>>(context) {
                            override fun onResponse(response: ResponsePacket<String>) {
                                Toast.makeText(context, "response:" + response.message, Toast.LENGTH_SHORT).show()
                            }
                        })

            R.id.buttonBzError ->
                api.bzError()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ObserverWithBZ<ResponsePacket<String>>(context) {
                            override fun onResponse(response: ResponsePacket<String>) {
                                Toast.makeText(context, "response:" + response.message, Toast.LENGTH_SHORT).show()
                            }
                        })

            R.id.buttonAuthorizeFailed ->
                api.authorizeFailed()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ObserverWithBZ<ResponsePacket<String>>(context) {
                            override fun onResponse(response: ResponsePacket<String>) {
                                Toast.makeText(context, "response:" + response.message, Toast.LENGTH_SHORT).show()
                            }
                        })
        }
    }
}

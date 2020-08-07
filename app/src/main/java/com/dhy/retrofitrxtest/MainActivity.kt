package com.dhy.retrofitrxtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.retrofitrxutil.delayResponse
import com.dhy.retrofitrxutil.subscribeX
import com.dhy.retrofitrxutil.subscribeXBuilder
import com.dhy.xintent.Waterfall
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var context: Context
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
        val apiSample = api.simple()
        buttonMultReq.setOnClickListener {
            Waterfall.flow {
                apiSample.subscribeX(context) {
                    Log.i("TAG", "apiSample1")
                    next()
                }
            }.flow {
                apiSample.subscribeX(context) {
                    Log.i("TAG", "apiSample2")
                    next()
                }
            }.flow {
                apiSample.subscribeX(context) {
                    Log.i("TAG", "apiSample3")
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        buttonFinish.setOnClickListener {
            apiSample.subscribeX(context) {
                Log.i("TAG", "apiSample")
            }
            buttonFinish.postDelayed({
                finish()
            }, 500)
        }

        buttonDelay.setOnClickListener {
            val start = System.currentTimeMillis()
            apiSample.delayResponse(5000)
                    .subscribeX(context) {
                        val cost = System.currentTimeMillis() - start
                        Log.i("TAG", "apiSample cost $cost")
                    }
        }
    }

    private fun initApi() {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(MockDataApiInterceptor()).build()
        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl("https://www.demo.com/")
                .build()
        api = retrofit.create(API::class.java)
    }

    @Suppress("unused")
    private fun subscribeX() {
        api.simple().subscribeX(context) {
            Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
        }

        api.simple().subscribeXBuilder(context)
                .failed {
                    true
                }.response {

                }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonOK ->
                api.simple().subscribeX(context) {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }

            R.id.buttonNetError ->
                api.netError().subscribeX(context) {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }

            R.id.buttonBzError ->
                api.bzError().subscribeX(context) {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }

            R.id.buttonAuthorizeFailed ->
                api.authorizeFailed().subscribeX(context) {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}

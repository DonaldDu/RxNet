package com.dhy.retrofitrxtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.retrofitrxutil.*
import com.dhy.xintent.Waterfall
import com.dhy.xintent.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope {
    private lateinit var job: Job
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
        buttonMultReq.setOnClickListener {
            //串行请求，最后一个结束的关闭进度框，中间不闪烁
            Waterfall.flow {
                api.simple().subscribeX(context) {
                    Log.i("TAG", "apiSample1")
                    next()//进入下一个flow，可以带任意类型的数据如：next("DATA")
                }
            }.flow {
                api.simple().subscribeX(context) {
                    Log.i("TAG", "apiSample2")
                    next()
                }
            }.flow {
                api.simple().subscribeX(context) {
                    Log.i("TAG", "apiSample3")
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btParalleleReq.setOnClickListener {
            //并发请求，最后一个结束的关闭进度框
            api.simple().subscribeX(context) {
                Log.i("TAG", "apiSample1")
                Toast.makeText(context, "response1:" + it.message, Toast.LENGTH_SHORT).show()
            }
            buttonDelay.performClick()
        }

        buttonFinish.setOnClickListener {
            showProgress()
            api.simple().subscribeX(context) {
                dismissProgress()
                Log.i("TAG", "api.simple()")
            }
            api.simple().subscribeX(context) { }
            api.simple().subscribeX(context) { }
            api.simple().subscribeX(context) { }
            api.simple().subscribeX(context) { }
            api.simple().subscribeX(context) { }
            buttonFinish.postDelayed({
                finish()
            }, 500)
        }

        buttonDelay.setOnClickListener {
            val start = System.currentTimeMillis()
            api.simple().delayResponse(3000)
                    .subscribeX(context) {
                        val cost = System.currentTimeMillis() - start
                        Log.i("TAG", "api.simple() cost $cost")
                        Toast.makeText(context, "delayResponse cost $cost", Toast.LENGTH_SHORT).show()
                    }
        }
        job = Job()
        btCoroutinesTest.setOnClickListener {
            launch {
                showProgress()
                val result = api.coroutinesTest()
                dismissProgress()
                toast("message " + result.message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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
        //简单模式，通常都用这个
        api.simple().subscribeX(context) {
            Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
        }
        //构造器模式，看名字都基本能知道功能了
        api.simple().subscribeXBuilder(context)
                .progress {
                    null//null means no default and custom progress
                }.failed {
                    true//return error handled or not
                }.successOnly(true)
                .response {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
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

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
}

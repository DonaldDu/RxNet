package com.dhy.retrofitrxtest

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface API {
    @GET("Simple?net=1&bz=1")
    fun simple(): Observable<ResponsePacket<String>>

    @GET("NetError?net=0&bz=1")
    fun netError(): Observable<ResponsePacket<String>>

    @GET("BzError?net=1&bz=0")
    fun bzError(): Observable<ResponsePacket<String>>

    @GET("authorizeFailed")
    fun authorizeFailed(): Observable<ResponsePacket<String>>

    @GET("Simple?net=1&bz=1")
    suspend fun coroutinesTest(): ResponsePacket<String>
}
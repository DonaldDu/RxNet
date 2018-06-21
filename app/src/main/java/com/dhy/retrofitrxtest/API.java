package com.dhy.retrofitrxtest;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface API {
    @GET("Simple?net=1&bz=1")
    Observable<ResponsePacket<String>> simple();

    @GET("NetError?net=0&bz=1")
    Observable<ResponsePacket<String>> netError();

    @GET("BzError?net=1&bz=0")
    Observable<ResponsePacket<String>> bzError();

    @GET("authorizeFailed")
    Observable<ResponsePacket<String>> authorizeFailed();
}

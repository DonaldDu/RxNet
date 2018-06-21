package com.dhy.retrofitrxtest;

import com.dhy.retrofitrxtest.data.UserPermission;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TestingApi {
    @GET("api/v1/sys/sysResourceInfo/currentResource")
    Observable<ResponsePacket<UserPermission[]>> fetchUserPermissions(@Header("token") String token);
}

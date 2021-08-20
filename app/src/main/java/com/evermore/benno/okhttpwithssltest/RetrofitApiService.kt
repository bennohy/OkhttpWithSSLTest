package com.evermore.benno.okhttpwithssltest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface RetrofitApiService {

    @GET("admin/userList")
    fun getUserList(): Call<List<User>>

    @GET("admin/trustedDevices")
    fun getDeviceList(): Call<DeviceResponse>

    @GET("admin/trustedDevices/{deviceId}")
    fun getDevice(
        @Path("deviceId")
        deviceId: String
    ): Call<List<Device>>
}
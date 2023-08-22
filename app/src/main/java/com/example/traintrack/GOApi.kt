package com.example.traintrack

import com.example.traintrack.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GOApi {
    @GET("api/V1/Stop/NextService/mo")
    suspend fun getData(@Query("key") key: String): ApiResponse

    @GET("api/V1/Stop/NextService/{stopCode}")
    suspend fun getDataCustom(
        @Path("stopCode") code: String,
        @Query("key") key: String
    ): ApiResponse
}
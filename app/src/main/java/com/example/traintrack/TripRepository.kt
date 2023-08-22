package com.example.traintrack

import com.example.traintrack.model.ApiResponse

class TripRepository {
    private val getTripService = RetrofitInstance.getTripService


    suspend fun getTrips(): ApiResponse {
        return getTripService.getData(key = privateKeys.apiKey)
    }

    // must be suspend fun so can run as coroutine
    suspend fun getTripsCustom(code: String): ApiResponse {
        return getTripService.getDataCustom(code = code, key = privateKeys.apiKey)
    }
}
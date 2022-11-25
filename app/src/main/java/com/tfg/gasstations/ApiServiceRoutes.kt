package com.tfg.gasstations

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceRoutes {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") api_key : String,
        @Query("start", encoded = true) start : String,
        @Query("end", encoded = true) end: String
    ) : Response<RouteResponse>
}
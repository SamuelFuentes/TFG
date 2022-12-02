package com.tfg.gasstations.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.tfg.gasstations.data.model.routes.RouteResponse

interface ApiServiceRoutes {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") api_key : String,
        @Query("start", encoded = true) start : String,
        @Query("end", encoded = true) end: String
    ) : Response<RouteResponse>
}
package com.tfg.gasstations.core

import com.tfg.gasstations.data.model.routes.RouteResponse
import com.tfg.gasstations.data.network.ApiServiceRoutes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getApiGas(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://sedeaplicaciones.minetur.gob.es/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getApiRoutes() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    suspend fun createRoute(start: String, end: String): RouteResponse?{
        val call = RetrofitHelper.getApiRoutes().create(ApiServiceRoutes::class.java)
            .getRoute("5b3ce3597851110001cf624863cc2b9245844cacb6cf551d2d8490c1",
                start,end)
        return call.body()
    }
}
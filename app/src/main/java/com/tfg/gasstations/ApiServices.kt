package com.tfg.gasstations

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Objects

interface ApiServiceCities {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Provincias/")
    suspend fun getCities() : Response<Array<CitiesResponse>>
}
interface ApiServiceGasByCity {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvincia/{IDProvincia}")
    suspend fun getGasStationsByCity(
        @Path("IDProvincia") IDProvincia : String
    ): Response<GasStationsResponse>
}

interface ApiServiceRoutes {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") api_key : String,
        @Query("start", encoded = true) start : String,
        @Query("end", encoded = true) end: String
    ) : Response<RouteResponse>
}
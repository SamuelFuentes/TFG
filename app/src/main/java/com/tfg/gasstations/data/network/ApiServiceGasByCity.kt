package com.tfg.gasstations.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.tfg.gasstations.data.model.gas.GasStationsResponse

interface ApiServiceGasByCity {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvincia/{IDProvincia}")
    suspend fun getGasStationsByCity(
        @Path("IDProvincia") IDProvincia : String
    ): Response<GasStationsResponse>
}
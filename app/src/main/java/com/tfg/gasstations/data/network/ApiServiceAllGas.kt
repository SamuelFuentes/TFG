package com.tfg.gasstations.data.network

import com.tfg.gasstations.data.model.gas.GasStationsResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceAllGas {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/")
    suspend fun getAllGasStations(): Response<GasStationsResponse>
}
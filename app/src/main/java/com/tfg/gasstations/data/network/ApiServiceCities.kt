package com.tfg.gasstations.data.network

import retrofit2.Response
import retrofit2.http.GET
import com.tfg.gasstations.data.model.cities.CitiesResponse

interface ApiServiceCities {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Provincias/")
    suspend fun getCities() : Response<Array<CitiesResponse>>
}
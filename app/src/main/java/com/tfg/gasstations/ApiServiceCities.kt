package com.tfg.gasstations

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceCities {
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Provincias/")
    suspend fun getCities() :Response<Array<CitiesResponse>>
}
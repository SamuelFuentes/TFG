package com.tfg.gasstations.data.model.routes

import com.google.gson.annotations.SerializedName

data class GeometryResponse(@SerializedName("coordinates") val coordinates : List<List<Double>>)
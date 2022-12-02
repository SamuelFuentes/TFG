package com.tfg.gasstations.data.model.routes

import com.google.gson.annotations.SerializedName
import com.tfg.gasstations.data.model.routes.GeometryResponse

data class FeatureResponse(@SerializedName("geometry") val geometry : GeometryResponse)
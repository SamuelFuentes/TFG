package com.tfg.gasstations.data.model.routes

import com.google.gson.annotations.SerializedName
import com.tfg.gasstations.data.model.routes.FeatureResponse

data class RouteResponse(@SerializedName("features") val features : List<FeatureResponse>)
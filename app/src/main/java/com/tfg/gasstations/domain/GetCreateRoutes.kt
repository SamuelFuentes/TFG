package com.tfg.gasstations.domain

import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.network.ApiServiceRoutes
import com.tfg.gasstations.data.model.routes.RouteResponse


class GetCreateRoutes {
    suspend fun createRoute(start: String, end: String): RouteResponse?{
        val call = RetrofitHelper.getApiRoutes().create(ApiServiceRoutes::class.java)
            .getRoute("5b3ce3597851110001cf624863cc2b9245844cacb6cf551d2d8490c1",
                start,end)
        return call.body()
    }
}
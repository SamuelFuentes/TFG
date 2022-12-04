package com.tfg.gasstations.domain

class GetNearPos {
    //Calcula si están proximos 2 coordenadas
    fun calculateNear(lat1: Double, lon1: Double, lat2: Double, lon2: Double, distance: String):Boolean{
        var resultado : Boolean = false
        var resLon = lon1-lon2
        if(resLon<0){ resLon *= -1 }
        var resLat = lat1-lat2
        if(resLat<0){ resLat*=-1 }
        var res = resLat+resLon
        if(distance == "1km" && res< 0.01 && res > -0.01){ resultado = true }
        if(distance == "2km" && res< 0.025 && res > -0.025){ resultado = true }
        if(distance == "6km" && res< 0.05 && res > -0.05){ resultado = true }
        return resultado
    }
    //~1,km --> 0.01
    //~2,5km --> 0.025
    //~6,5km --> 0.05

}
package com.tfg.gasstations.domain

import android.util.Log

class GetNearPos {
    //calcula si están proximos 2 coordenadas
    fun calculateNear(lat1: Double, lon1: Double, lat2: Double, lon2: Double):Boolean{
        Log.i("DEPURANDO","lat1 "+lat1.toString())
        Log.i("DEPURANDO","lat2 "+lat2.toString())
        Log.i("DEPURANDO","lon1 "+lon1.toString())
        Log.i("DEPURANDO","lon2 "+lon2.toString())
        var resultado : Boolean = false
        var resLon = lon1-lon2
        Log.i("DEPURANDO","RES long "+resLon.toString())
        if(resLon<0){
            resLon *= -1
            Log.i("DEPURANDO","RES long CAMBIAO"+resLon.toString())
        }
        var resLat = lat1-lat2
        Log.i("DEPURANDO", "RES lat "+resLat.toString())
        if(resLat<0){
            resLat*=-1
            Log.i("DEPURANDO","RES lat CAMBIAO "+resLat.toString())
        }
        var res = resLat+resLon
        Log.i("DEPURANDO","RES "+res.toString())
        if(res< 0.05 && res > -0.05){
            Log.i("DEPURANDO", res.toString()+" Ta cerca")
            resultado = true
        }
        return resultado
    }
    //1,km --> 0.01
    //2,5km --> 0.025
    //6,5km --> 0.05

}
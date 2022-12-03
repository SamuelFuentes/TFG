package com.tfg.gasstations.domain

import com.google.android.gms.maps.model.LatLng

class GetApiLatLng {
    //Convierte las coordenadas de latitud y longitud de la api en LatLng utilizables por googleMaps
    fun toLatLng(lat : String, lng : String): LatLng {
        return LatLng(lat.replace(",",".").toDouble(),
            lng.replace(",",".").toDouble())
    }
}
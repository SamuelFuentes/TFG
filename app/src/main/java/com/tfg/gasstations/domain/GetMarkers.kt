package com.tfg.gasstations.domain

import android.graphics.Bitmap
import android.util.Log
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GetMarkers {
    fun markerAll(map: GoogleMap, gas95Price: String, gasoilPrice: String, position: LatLng,
                          schedule: String, label: String, address: String){
        var gasTypeForSnippet = GetHavePrice().price(gas95Price, gasoilPrice)
        map.addMarker(
            MarkerOptions().position(position).title("${label}, ${address}")
            .snippet(schedule+" | "+gasTypeForSnippet[0]+gasTypeForSnippet[1])
            .icon(GetMarkerIcon().select(label)))
    }
    fun markerByGas(map: GoogleMap, gasPrice: String, position: LatLng, schedule: String,
        label: String, address: String, minPrice: Double, textMin: TextView, textNormal: TextView){
        Log.i("DEPURANDO","SE ESTA USANDO")
        var imageViewShell : TextView = textNormal
        if(minPrice== gasPrice.replace(",",".").toDouble()){
            imageViewShell = textMin
        }else{
            imageViewShell = textNormal
        }
        imageViewShell.text = gasPrice+"€"
        val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
            .viewTextToBitmap(imageViewShell), 136, 136, false)
        map.addMarker(MarkerOptions().position(position).title(
            "${label}, ${address}").snippet("Horario: " + schedule)
            .icon(
                BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                (bitmapShell, 136,136,false))))
        Log.i("DEPURANDO","Salio")
        if(minPrice== gasPrice.replace(",",".").toDouble()){
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(position, 16f),
                4000,
                null)
        }
    }
    fun selectTypeAndMark(type: String, map: GoogleMap, gas95Price: String, gasoilPrice: String,
                   position: LatLng, schedule: String, label: String, address: String,
                   minPriceGas95: Double, minPriceGasoil: Double, textMin: TextView,
                   textNormal: TextView){
        if(type=="ALL"){
            markerAll(map, gas95Price, gasoilPrice, position, schedule,
                label, address)
        }
        if(type=="GAS95" && gas95Price.isNotEmpty()){
            markerByGas(map, gas95Price, position, schedule, label,
                address, minPriceGas95, textMin, textNormal )
        }
        if(type=="GASOIL" && gasoilPrice.isNotEmpty()){
            markerByGas(map, gasoilPrice, position, schedule, label,
                address, minPriceGasoil, textMin, textNormal )
        }

    }
}
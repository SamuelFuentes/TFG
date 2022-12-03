package com.tfg.gasstations.domain

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.tfg.gasstations.R

class GetMarkerIcon {
    fun select(label : String): BitmapDescriptor {
        var markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_gasstations32x32)
        if(label.contains("AVIA")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_avia32x32)
        }
        if(label.contains("BP")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_bp32x32)
        }
        if(label.contains("CARREFOUR")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_carrefour32x32)
        }
        if(label.contains("CEPSA")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_cepsa32x32)
        }
        if(label.contains("DISA")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_disa32x32)
        }
        if(label.contains("EROSKI")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_eroski32x32)
        }
        if(label.contains("GALP")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_galp32x32)
        }
        if(label.contains("HAM")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_ham32x32)
        }
        if(label.contains("NATURGY")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_naturgy32x32)
        }
        if(label.contains("PETRONOR")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_petronor32x32)
        }
        if(label.contains("REPSOL")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_repsol32x32)
        }
        if(label.contains("SHELL")){
            markerRes = BitmapDescriptorFactory.fromResource(R.drawable.marker_shell32x32)
        }
        return markerRes
    }
}
package com.tfg.gasstations.data.model.gas

import com.google.gson.annotations.SerializedName

data class GasListResponse(@SerializedName("Dirección") var address : String,
                           @SerializedName("Horario") var schedule : String,
                           @SerializedName("Latitud") var lati : String,
                           @SerializedName("Longitud (WGS84)") var long : String,
                           @SerializedName("Precio Gasoleo A") var gasol : String,
                           @SerializedName("Precio Gasolina 95 E5") var gas95 : String,
                           @SerializedName("Precio Gasolina 98 E5") var gas98 : String,
                           @SerializedName("Rótulo") var label : String)
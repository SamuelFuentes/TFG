package com.tfg.gasstations.data.model.cities

import com.google.gson.annotations.SerializedName

data class CitiesResponse(@SerializedName("IDPovincia") var idProvincia : String,
                          @SerializedName("Provincia") var provincia : String)
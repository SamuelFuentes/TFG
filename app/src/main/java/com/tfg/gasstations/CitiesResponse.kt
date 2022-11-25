package com.tfg.gasstations

import com.google.gson.annotations.SerializedName

data class CitiesResponse(@SerializedName("IDPovincia") var idPovincia : String ,
                          @SerializedName("Provincia") var provincia : String)

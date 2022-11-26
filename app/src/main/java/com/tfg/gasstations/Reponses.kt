package com.tfg.gasstations

import com.google.gson.annotations.SerializedName

//Ciudades
data class CitiesResponse(@SerializedName("IDPovincia") var idProvincia : String ,
                          @SerializedName("Provincia") var provincia : String)
//Gasolineras
data class GasStationsResponse(@SerializedName("Dirección") var address : String,
                               @SerializedName("Horario") var schedule : String,
                               @SerializedName("Latitud") var lati : String,
                               @SerializedName("Longitud") var long : String,
                               @SerializedName("Precio_Gasoleo_A") var gasoleo : String,
                               @SerializedName("Precio_Gasolina_95_E5") var gas95 : String,
                               @SerializedName("Precio_Gasolina_98_E5") var gas98 : String,
                               @SerializedName("Rótulo") var label : String)
//Rutas
data class RouteResponse(val features : List<Feature>)
data class Feature(val geometry : Geometry)
data class Geometry(val coordinates : List<List<Double>>)
package com.tfg.gasstations

import com.google.gson.annotations.SerializedName

//Ciudades
data class CitiesResponse(@SerializedName("IDPovincia") var idProvincia : String ,
                          @SerializedName("Provincia") var provincia : String)
//Gasolineras
data class GasStationsResponse(@SerializedName("ListaEESSPrecio") var gasList : Array<GasList>)
data class GasList(@SerializedName("Dirección") var address : String,
                   @SerializedName("Horario") var schedule : String,
                   @SerializedName("Latitud") var lati : String,
                   @SerializedName("Longitud (WGS84)") var long : String,
                   @SerializedName("Precio Gasoleo A") var gasol : String,
                   @SerializedName("Precio Gasolina 95 E5") var gas95 : String,
                   @SerializedName("Precio Gasolina 98 E5") var gas98 : String,
                   @SerializedName("Rótulo") var label : String)
//Rutas
data class RouteResponse(val features : List<Feature>)
data class Feature(val geometry : Geometry)
data class Geometry(val coordinates : List<List<Double>>)
package com.tfg.gasstations.domain

import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.network.ApiServiceCities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetCities {
    //Crear lista de ciudades para mostrarlas en el Spinner
    fun listCities(): List<String> {
        val citiesArrayList : MutableList<String> = mutableListOf("")
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitHelper.getApiGas().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                for (i in call.body()!!) {
                    citiesArrayList.add(i.provincia)
                }
            }
        }
        return citiesArrayList
    }
    //Buscar la id de la ciudad elegida en el Spinner
    suspend fun searchIdCity(city : String): String{
        var res: String = "00"
        val call = RetrofitHelper.getApiGas().create(ApiServiceCities::class.java).getCities()
        if (call.isSuccessful) {
            for (i in call.body()!!) {
                if(city == i.provincia){
                    res = i.idProvincia
                }
            }
        }
        return res
    }
}
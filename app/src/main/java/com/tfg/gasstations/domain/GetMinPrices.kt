package com.tfg.gasstations.domain

import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.network.ApiServiceGasByCity

class GetMinPrices() {
    //Obtener el precio mínimo de la gasolina95 filtrado por ciudad
    suspend fun minPrice95(idSelectedCity: String): Double {
        var lastMinPrice95 : Double = 99.99
        val call = RetrofitHelper.getApiGas().create(ApiServiceGasByCity::class.java)
            .getGasStationsByCity(idSelectedCity)
        if (call.isSuccessful) {
            for (i in call.body()!!.gasList) {
                if(i.gas95.isNotEmpty()){
                    if (lastMinPrice95 > i.gas95.replace(",",".").toDouble()){
                        lastMinPrice95 = i.gas95.replace(",",".").toDouble()
                    }
                }
            }
        }
        return lastMinPrice95
    }
    //Obtener el precio mínimo del gasoil filtrado por ciudad
    suspend fun minPriceGasoil(idSelectedCity: String): Double {
        var lastMinPriceGasoil : Double = 99.99
            val call = RetrofitHelper.getApiGas().create(ApiServiceGasByCity::class.java)
                .getGasStationsByCity(idSelectedCity)
            if (call.isSuccessful) {
                for (i in call.body()!!.gasList) {
                    if(i.gasol.isNotEmpty()){
                        if (lastMinPriceGasoil > i.gasol.replace(",",".").toDouble()){
                            lastMinPriceGasoil = i.gasol.replace(",",".").toDouble()
                        }
                    }
            }
        }
        return lastMinPriceGasoil
    }
    //Obtener el precio mínimo de cualquier combustible
    fun minPrice(last: Double, new: Double): Double{
        var res: Double = last
        if(last>new){
            res = new
        }
        return res
    }
}
package com.tfg.gasstations.domain

import android.util.Log
import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.network.ApiServiceGasByCity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetMinPrices() {
    //Obtener el precio mínimo de cada combustible
    suspend fun minPrice95(idSelectedCity: String): Double {
        var lastMinPrice95 : Double = 99.99
        val call = RetrofitHelper.getApiGas().create(ApiServiceGasByCity::class.java)
            .getGasStationsByCity(idSelectedCity)
        if (call.isSuccessful) {
            Log.i("DEPURANDO", "SUCCES")
            for (i in call.body()!!.gasList) {
                if(i.gas95.isNotEmpty()){
                    if (lastMinPrice95 > i.gas95.replace(",",".").toDouble()){
                        lastMinPrice95 = i.gas95.replace(",",".").toDouble()
                    }
                }
            }
        }
        Log.i("DEPURANDO", "last"+ lastMinPrice95)
        return lastMinPrice95
    }
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
        Log.i("DEPURANDO", "last"+ lastMinPriceGasoil)
        return lastMinPriceGasoil
    }
}
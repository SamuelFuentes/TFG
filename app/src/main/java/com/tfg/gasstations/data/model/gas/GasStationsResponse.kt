package com.tfg.gasstations.data.model.gas

import com.google.gson.annotations.SerializedName
import com.tfg.gasstations.data.model.gas.GasListResponse

data class GasStationsResponse(@SerializedName("ListaEESSPrecio") var gasList : Array<GasListResponse>)
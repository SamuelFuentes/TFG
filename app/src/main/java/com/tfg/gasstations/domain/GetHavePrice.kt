package com.tfg.gasstations.domain

class GetHavePrice {
    //Mostrará unicamente los productos que tengan el precio en la API
     fun price(apiGas95 : String, apigasol : String):ArrayList<String>{
        var gas95 = ""
        var gasol = ""
        val gasTypeArrayList = ArrayList<String>()
        if(apiGas95.isNotEmpty()){ gas95 = "Gas 95: "+ apiGas95 +"€ " }
        if(apigasol.isNotEmpty()){ gasol = "Gasoleo: "+ apigasol +"€ " }
        gasTypeArrayList.add(gas95)
        gasTypeArrayList.add(gasol)
        return gasTypeArrayList
    }
}
package com.tfg.gasstations

data class RouteResponse(val features : List<Feature>)
data class Feature(val geometry : Geometry)
data class Geometry(val coordinates : List<List<Double>>)
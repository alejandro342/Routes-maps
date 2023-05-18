package com.alejandro.routesmap.model

import com.google.gson.annotations.SerializedName

data class RoutesResponse(@SerializedName("features") val features: List<Feature>)
data class Feature(@SerializedName("geometry") val geometry: Geometry)
data class Geometry(@SerializedName("coordinates") val coordinate: List<List<Double>>)
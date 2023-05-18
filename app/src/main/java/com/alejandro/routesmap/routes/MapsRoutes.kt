package com.alejandro.linksstore.routes


import com.alejandro.routesmap.model.RoutesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsRoutes {

    @GET("/v2/directions/driving-car")
    suspend fun getRoutesMaps(
        @Query("api_key") key: String,
        @Query("start", encoded = true) start: String,//para pasar un coma se le agrega encode=true
        @Query("end", encoded = true) end: String
    ): Response<RoutesResponse>?

}


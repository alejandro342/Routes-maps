package com.alejandro.linksstore.api.routes

import com.alejandro.linksstore.api.retrofit.RetrofitClient
import com.alejandro.linksstore.routes.MapsRoutes

class ApiRoutes {
    private val BASE_URL = "https://api.openrouteservice.org/"
    private val retrofit = RetrofitClient()

    fun getRoutesMaps():MapsRoutes{
        return retrofit.getClient(BASE_URL).create(MapsRoutes::class.java)
    }
}
package com.alejandro.linksstore.providers

import com.alejandro.linksstore.api.routes.ApiRoutes
import com.alejandro.linksstore.routes.MapsRoutes
import com.alejandro.routesmap.model.RoutesResponse
import retrofit2.Response

class RoutesMapsProvider {
    private var mRoutesMaps:MapsRoutes?=null
    init {
        val api=ApiRoutes()
        mRoutesMaps = api.getRoutesMaps()
    }
    suspend fun getRoutesMaps(key:String, start:String, end:String): Response<RoutesResponse>? {
        return mRoutesMaps?.getRoutesMaps(key, start, end)
    }
}
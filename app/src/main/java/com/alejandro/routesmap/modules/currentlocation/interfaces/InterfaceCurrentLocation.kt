package com.alejandro.routesmap.modules.currentlocation.interfaces

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions

interface InterfaceCurrentLocation {
    fun addMarkerToMap(markerOptions: MarkerOptions)
    fun getGoogleMap(): GoogleMap
}
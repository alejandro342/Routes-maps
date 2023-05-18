package com.alejandro.routesmap.modules.currentlocation.presenter

import android.annotation.SuppressLint
import android.app.Activity
import com.alejandro.routesmap.R
import com.alejandro.routesmap.modules.currentlocation.interfaces.InterfaceCurrentLocation
import com.alejandro.routesmap.permisionlocation.LocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PresenterCurrent(activity: Activity, var mapInterfaceCurrentLocation: InterfaceCurrentLocation) {

    var googleMap: GoogleMap?=null
    private var fusedLocationClient: FusedLocationProviderClient
    var activity: Activity? = null

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private var mMarkerMyLocation: Marker? = null

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        this.activity = activity
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
         googleMap = mapInterfaceCurrentLocation.getGoogleMap()
        if (LocationPermission.hasLocationPermissions(activity!!)) {
            if (LocationPermission.isLocationEnable(activity!!)) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        val myLocation = LatLng(location.latitude, location.longitude)
                        addMarkerLocation(googleMap!!, latitude, longitude)
                        moveCamera(googleMap!!, myLocation)
                    }
                }
            } else {
                //mandar al usuario ah que habilite el gps
                LocationPermission.settings(activity!!)
            }
        } else {
            //si los permisos no estan habilitados
            LocationPermission.requestLocationPermissions(activity!!)
        }
    }

    fun addMarkerLocation(googleMap: GoogleMap, lat: Double, lng: Double) {
        val location = LatLng(lat, lng)

        mMarkerMyLocation?.remove()
        mMarkerMyLocation = googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )
    }

    private fun moveCamera(googleMap: GoogleMap, location: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}































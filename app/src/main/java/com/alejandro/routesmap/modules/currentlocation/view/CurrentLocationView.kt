package com.alejandro.routesmap.modules.currentlocation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alejandro.routesmap.R
import com.alejandro.routesmap.databinding.ActivityCurrentLocationViewBinding
import com.alejandro.routesmap.modules.currentlocation.interfaces.InterfaceCurrentLocation
import com.alejandro.routesmap.modules.currentlocation.presenter.PresenterCurrent
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class CurrentLocationView : AppCompatActivity(), OnMapReadyCallback, InterfaceCurrentLocation {

    private var mBinding: ActivityCurrentLocationViewBinding? = null

    //para la integración del mapa de google
    private lateinit var mGoogleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var mPresenterCurrent: PresenterCurrent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCurrentLocationViewBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mPresenterCurrent = PresenterCurrent(this, this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapRoute) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        mPresenterCurrent?.googleMap = mGoogleMap
        mPresenterCurrent?.getLocation()
    }


    override fun addMarkerToMap(markerOptions: MarkerOptions) {
        mGoogleMap.addMarker(markerOptions)
    }

    override fun getGoogleMap(): GoogleMap {
        return mGoogleMap
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}

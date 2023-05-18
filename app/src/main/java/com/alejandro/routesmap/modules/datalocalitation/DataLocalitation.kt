package com.alejandro.routesmap.modules.datalocalitation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.alejandro.routesmap.R
import com.alejandro.routesmap.databinding.ActivityDataLocalitationBinding
import com.alejandro.routesmap.modules.datalocalitation.fragment.BottomSheetData
import com.alejandro.routesmap.permisionlocation.LocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.roundToInt

class DataLocalitation : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    var mBinding: ActivityDataLocalitationBinding? = null

    //para la integración del mapa de google
    private var mGoogleMap: GoogleMap? = null

    /* obtener los datos de la localizacion del mapa */
    var mCity = ""
    var mCountry = ""
    var mAddress = ""
    var mAddressLatLng: LatLng? = null
    private val PERMISSION_ID = 7
    private var mMarkerPointA: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /* obtener la ubicación actual */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }
    var bottomSheetData = BottomSheetData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDataLocalitationBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        mBinding!!.BtnGetData.setOnClickListener(this)
        mapInf()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    fun mapInf() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapRoute) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //para la integración del mapa de google para el mapa cargado
    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        //llamamos al metodo para mostrar los datos
        onCameraMove()

    }

    /* obtener los datos de la localizacion del mapa */
    @SuppressLint("SetTextI18n")
    private fun onCameraMove() {

        mGoogleMap?.setOnCameraIdleListener {
            try {
                mMarkerPointA?.remove()
                val mGeocoder = Geocoder(this)
                mAddressLatLng = mGoogleMap?.cameraPosition?.target
                val mAddressList = mGeocoder.getFromLocation(
                    mAddressLatLng?.latitude!!,
                    mAddressLatLng?.longitude!!,
                    1
                )
                //obtener la ciudad
                mCity = mAddressList?.get(0)?.locality.toString()
                mCountry = mAddressList?.get(0)?.countryName.toString()
                mAddress = mAddressList?.get(0)?.getAddressLine(0).toString()
                addMarkerLocation(mAddressLatLng?.latitude!!, mAddressLatLng?.longitude!!)

            } catch (e: Exception) {
                Log.d("TAG", "Error : ${e.message}")
            }
        }
    }


    /* obtener la ubicación actual */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        //verificar los permisos
        if (LocationPermission.hasLocationPermissions(this)) {
            //validar localizacion
            if (LocationPermission.isLocationEnable(this)) {
                mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    //obtener localizacion
                    val location = task.result
                    //validamos que no venga null
                    if (location == null) {
                        //si viene null llamamos
                        requestNewLocationData()
                    } else {
                        //ni no viene vacia movemos la camara
                        mGoogleMap?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder().target(
                                    LatLng(location.latitude, location.longitude)
                                ).zoom(15f).build()
                            )
                        )
                    }
                }
            } else {
                //mandar al usuario ah que habilite el gps
                Toast.makeText(this, "Habilite la localizacion", Toast.LENGTH_LONG).show()
                val mInten = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(mInten)
            }
        } else {
            //si los permisos no estan habilitados
            LocationPermission.requestLocationPermissions(this)
        }
    }

    /* obtener la ubicación actual obtener nueva localizacion */
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            //actualizar los datos de la localizasion modificar valores para ahorro de energia
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //escuchador de la localizacion en tiempo real
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mFusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    /* obtener la ubicación actual */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    fun getData() {
        val mLatitude = mAddressLatLng?.latitude as Double
        val formatLat = (mLatitude * 10000.0).roundToInt() / 10000.0
        val mLongitude = mAddressLatLng?.longitude as Double
        val formatLong = (mLongitude * 10000.0).roundToInt() / 10000.0
        val mCity = mCity
        val mCountry = mCountry
        bottomSheetData = BottomSheetData.newInstance(formatLat, formatLong, mCity, mCountry)
        bottomSheetData.show(supportFragmentManager, "xD")
    }


    private fun addMarkerLocation(lat: Double, lng: Double) {
        val location = LatLng(lat, lng)

        mMarkerPointA = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.BtnGetData -> getData()
        }
    }

}
package com.alejandro.routesmap.modules.routesmapabc.views


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.alejandro.linksstore.extenciones.myToast
import com.alejandro.routesmap.R
import com.alejandro.routesmap.databinding.ActivityRoutesAbcBinding
import com.alejandro.routesmap.permisionlocation.LocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class RoutesABC : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    var mBinding: ActivityRoutesAbcBinding? = null

    var location: Location? = null

    //para la integración del mapa de google
    private lateinit var map: GoogleMap

    //obtener localizacion actual
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 7
    private var mMarkerPointA: Marker? = null

    var mPointA = ""
    private var mPointB = ""
    private var mPointC = ""


    //guardar la ubicación
    private var mLocationLatLng: LatLng? = null

    private var mMarkerPointB: Marker? = null
    private var mMarkerPointC: Marker? = null

    //para borrar la ruta enterior
    var poly: Polyline? = null

    var selectedPoints = mutableListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutesAbcBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mBinding!!.BtnTrazarRuta.setOnClickListener(this)

        mapInf()
    }

    fun mapInf() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapRoute) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        if (LocationPermission.hasLocationPermissions(this)) {
            if (LocationPermission.isLocationEnable(this)) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    location = task.result
                    if (location != null) {
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        mPointA = "${location!!.latitude}, ${location!!.longitude}"
                        mLocationLatLng = mPointA.split(",").let {
                            LatLng(
                                location!!.latitude,
                                location!!.longitude
                            )
                        }
                        selectedPoints.add(mLocationLatLng!!)
                        addMarkerLocation(latitude, longitude)
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(mLocationLatLng!!, 15f)
                        )

                        getRutas()
                    }
                }
            } else {
                //mandar al usuario ah que habilite el gps
                myToast("Habilite la localizacion")
                val mInten = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(mInten)
            }
        } else {
            //si los permisos no estan habilitados
            LocationPermission.requestLocationPermissions(this)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                myToast("Permiso denegado")
            }
        }
    }

    private fun addMarkerLocation(lat: Double, lng: Double) {
        val location = LatLng(lat, lng)

        mMarkerPointA = map.addMarker(
            MarkerOptions()
                .position(location)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )
    }

    fun getRutas() {
        mPointB = ""
        mPointC = ""
        poly?.remove()
        mBinding!!.BtnTrazarRuta.text = "Marca punto B"
        mMarkerPointB?.remove()
        mMarkerPointC?.remove()
        if (selectedPoints.size > 1){
            selectedPoints = selectedPoints.subList(0,1)
        }
        if (::map.isInitialized) {
            map.setOnMapClickListener {
                if (mPointA.isEmpty()){
                    getLocation()
                }else
                if (mPointB.isEmpty()) {
                    mPointB = "${it.longitude}, ${it.latitude}"
                    mLocationLatLng =
                        mPointB.split(", ").let { LatLng(it[1].toDouble(), it[0].toDouble()) }
                    mMarkerPointB = map.addMarker(
                        MarkerOptions()
                            .position(mLocationLatLng!!)
                            .title("Punto B")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointb))
                    )
                    selectedPoints.add(mLocationLatLng!!)
                    myToast("${mLocationLatLng}")
                    mBinding!!.BtnTrazarRuta.text = "Marca punto C"
                } else if (mPointC.isEmpty()) {
                    mPointC = "${it.longitude}, ${it.latitude}"
                    mLocationLatLng =
                        mPointC.split(", ").let { LatLng(it[1].toDouble(), it[0].toDouble()) }
                    mMarkerPointC = map.addMarker(
                        MarkerOptions()
                            .position(mLocationLatLng!!)
                            .title("Punto C")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointc))
                    )
                    selectedPoints.add(mLocationLatLng!!)
                    myToast("${mLocationLatLng}")
                    mBinding!!.BtnTrazarRuta.text = "Nuevas rutas"
                    drawableRoute()
                }
            }
        }


    }

    fun drawableRoute() {

        val polyLineOptions = PolylineOptions()

        for (point in selectedPoints) {
            val latitude = point.latitude
            val longitude = point.longitude

            polyLineOptions.add(LatLng(latitude, longitude))
            polyLineOptions.color(Color.BLUE)
            polyLineOptions.width(15f)
            polyLineOptions.pattern(listOf(Dot(), Gap(20f)))
        }

        //para regresar el hilo principal
        runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
        }
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.BtnTrazarRuta -> getLocation()
        }
    }
}
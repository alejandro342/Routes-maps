package com.alejandro.routesmap.modules.routesmap.views

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.linksstore.extenciones.myToast
import com.alejandro.linksstore.providers.RoutesMapsProvider
import com.alejandro.routesmap.R
import com.alejandro.routesmap.databinding.ActivityRouteMapsBinding
import com.alejandro.routesmap.model.RoutesResponse
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteMapsView :AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var map: GoogleMap

    private var mRoutesProvider: RoutesMapsProvider? = null
    private var mBinding: ActivityRouteMapsBinding? = null

    private var star = ""
    private var end = ""

    //para borrar la ruta enterior
    private var poly: Polyline? = null

    //marcador inicio y final
    private var mMarkerStart: Marker? = null
    private var mMarkerEnd: Marker? = null

    //guardar la ubicación
    private var mLocationLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRouteMapsBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mRoutesProvider = RoutesMapsProvider()
        mBinding!!.BtnTrazarRuta.setOnClickListener(this)
        mapInf()

    }

    private fun mapInf() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapRoute) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        moveCamera()
    }

    private fun createRoutes() {
        //ejecutar fuera del hilo principal
        CoroutineScope(Dispatchers.IO).launch {
            val call = mRoutesProvider?.getRoutesMaps(
                "tu_Key_de_open",
                star,
                end
            )
            if (call!!.isSuccessful) {
                Log.d("Alex", "OK")
                //pintar la ruta
                drawableRoute(call.body()!!)
                mBinding!!.BtnTrazarRuta.text="Nueva ruta"
            } else {
                Log.d("Alex", "KO")
            }
        }
    }

    //pintar ruta
    private fun drawableRoute(routesResponse: RoutesResponse?) {
        val polyLineOptions = PolylineOptions()
        routesResponse?.features?.first()?.geometry?.coordinate?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
            polyLineOptions.color(Color.RED)
            polyLineOptions.width(15f)
            polyLineOptions.pattern(listOf(Dot(), Gap(20f)))
        }
        //para regresar el hilo principal
        runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
        }
    }

    private fun getRutas() {
        star = ""
        end = ""
        mBinding!!.BtnTrazarRuta.text="Marca punto de inicio"
        //quitar ruta anterior
        poly?.remove()
        mMarkerStart?.remove()
        mMarkerEnd?.remove()
        poly = null
        if (::map.isInitialized) {
            map.setOnMapClickListener { it ->
                if (star.isEmpty()) {
                    star = "${it.longitude}, ${it.latitude}"
                    mLocationLatLng =
                        star.split(", ").let { LatLng(it[1].toDouble(), it[0].toDouble()) }
                    mMarkerStart = map.addMarker(
                        MarkerOptions()
                            .position(mLocationLatLng!!)
                            .title("Inicio")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                    )
                    mBinding!!.BtnTrazarRuta.text="Marca punto de llegada"
                } else if (end.isEmpty()) {
                    end = "${it.longitude}, ${it.latitude}"
                    mLocationLatLng =
                        end.split(", ").let { LatLng(it[1].toDouble(), it[0].toDouble()) }
                    mMarkerEnd = map.addMarker(
                        MarkerOptions()
                            .position(mLocationLatLng!!)
                            .title("casa")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    )
                    myToast("marcando ruta")
                    createRoutes()
                }
            }
        } else {
            myToast("El mapa no se ah cargado aun")
        }
    }

    private fun moveCamera() {
        val locationPre = LatLng(19.435207, -99.140840)
        val zoomLevel = 15f
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(locationPre, zoomLevel)
        )
    }

    override fun onClick(myItem: View?) {
        when (myItem) {
            mBinding!!.BtnTrazarRuta -> getRutas()
        }
    }
}

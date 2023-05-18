package com.alejandro.routesmap.modules.datalocalitation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alejandro.routesmap.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetData : BottomSheetDialogFragment() {

    var mTextL: TextView? = null
    var mTextCity: TextView? = null
    var mTextCountry:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val ARG_LATITUDE = "arg_latitude"
        private const val ARG_LONGITUDE = "arg_longitude"
        private const val ARG_CITY = "arg_city"
        private  const val ARG_COUNTRY="arg_country"


        fun newInstance(latitude: Double, longitude: Double, city:String,country:String): BottomSheetData {
            val fragment = BottomSheetData()
            val args = Bundle()
            args.putString(ARG_LATITUDE, latitude.toString())
            args.putString(ARG_LONGITUDE, longitude.toString())
            args.putString(ARG_CITY,city)
            args.putString(ARG_COUNTRY,country)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val latitude = arguments?.getString(ARG_LATITUDE)
        val longitude = arguments?.getString(ARG_LONGITUDE)
        val city=arguments?.getString(ARG_CITY)
        val country=arguments?.getString(ARG_COUNTRY)

        mTextCity=view.findViewById(R.id.TextCity)
        mTextL = view.findViewById(R.id.TextLatLong)
        mTextCountry=view.findViewById(R.id.TextCountry)
        mTextL?.text = "${latitude},${longitude}"
        mTextCity?.text=city
        mTextCountry?.text=country
    }
}
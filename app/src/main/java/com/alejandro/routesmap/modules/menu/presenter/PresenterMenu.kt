package com.alejandro.routesmap.modules.menu.presenter

import android.content.Context
import android.content.Intent
import com.alejandro.routesmap.modules.currentlocation.view.CurrentLocationView
import com.alejandro.routesmap.modules.datalocalitation.DataLocalitation
import com.alejandro.routesmap.modules.routesmap.views.RouteMapsView
import com.alejandro.routesmap.modules.routesmapabc.views.RoutesABC

class PresenterMenu(mContext: Context) {
    var mContext: Context? = null

    init {
        this.mContext = mContext
    }

    fun goToPointAB() {
        val mIntent = Intent(mContext, RouteMapsView::class.java)
        mContext?.startActivity(mIntent)
    }

    fun myLocation() {
        val mIntent = Intent(mContext, CurrentLocationView::class.java)
        mContext?.startActivity(mIntent)
    }

    fun getPointABC() {
        val mIntent = Intent(mContext, RoutesABC::class.java)
        mContext?.startActivity(mIntent)
    }

    fun getDataLocalitation() {
        val mIntent = Intent(mContext, DataLocalitation::class.java)
        mContext?.startActivity(mIntent)
    }
}
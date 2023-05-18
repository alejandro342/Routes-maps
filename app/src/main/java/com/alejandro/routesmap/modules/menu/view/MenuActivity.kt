package com.alejandro.routesmap.modules.menu.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.alejandro.routesmap.R
import com.alejandro.routesmap.databinding.ActivityMenuBinding
import com.alejandro.routesmap.modules.menu.presenter.PresenterMenu

class MenuActivity : AppCompatActivity(), View.OnClickListener {
    private var mBinding: ActivityMenuBinding? = null
    private var mPresenterMenu: PresenterMenu? = null
    private var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMenuBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mPresenterMenu = PresenterMenu(this)
        mBinding!!.PointAPointB.setOnClickListener(this)
        mBinding!!.myLocation.setOnClickListener(this)
        mBinding!!.PointAPointBPointC.setOnClickListener(this)
        mBinding!!.DataLocalitation.setOnClickListener(this)
        mToolbar = findViewById(R.id.toolbar)
        mToolbar()
    }

    fun mToolbar() {
        mToolbar?.title = "Menu"
        mToolbar?.titleMarginStart = 450
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.PointAPointB -> mPresenterMenu?.goToPointAB()
            mBinding!!.myLocation -> mPresenterMenu?.myLocation()
            mBinding!!.PointAPointBPointC -> mPresenterMenu?.getPointABC()
            mBinding!!.DataLocalitation -> mPresenterMenu?.getDataLocalitation()
        }
    }
}

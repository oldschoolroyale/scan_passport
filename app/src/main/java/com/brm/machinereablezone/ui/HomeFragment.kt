package com.brm.machinereablezone.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Rakhimjonov Shokhsulton on 19,июль,2022
 * at Mayasoft LLC,
 * Tashkent, UZB.
 */
open class HomeFragment : Fragment(){

    fun onRequestStart(){

    }

    fun onRequestComplete(){

    }

    fun onRequestError(message: Int){
        Toast.makeText(context, getString(message), Toast.LENGTH_LONG).show()
    }

}
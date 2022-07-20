package com.brm.machinereablezone.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rakhimjonov Shokhsulton on 19,июль,2022
 * at Mayasoft LLC,
 * Tashkent, UZB.
 */
@Parcelize
data class EntryData(
    var passportNumber: String? = null,
    var birthDate: String? = null,
    var issueDate: String? = null
): Parcelable
package com.brm.machinereablezone.core

import android.nfc.tech.IsoDep
import android.util.Log
import com.brm.machinereablezone.model.Person
import net.sf.scuba.smartcards.CardFileInputStream
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG1File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Rakhimjonov Shokhsulton on 19,июль,2022
 * at Mayasoft LLC,
 * Tashkent, UZB.
 */
object EntryPoint {

    private val TAG = this.javaClass.name

    fun parseData(isoDep: IsoDep, bacKey: BACKeySpec): Person? {
        Log.d(TAG, "parseData: $isoDep bacKey $bacKey")
        val cardService = CardService.getInstance(isoDep)
        cardService.open()

        val passService = PassportService(
            cardService,
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            true,
            false
        )
        var paceSucceeded = false
        try {
            Log.d(TAG, "parseData: trying cardSecurity")
            val cardSecurityFile = CardSecurityFile(passService.getInputStream(PassportService.EF_CARD_SECURITY))
            Log.d(TAG, "parseData: cardSecurity successful")
            val securityInfoCollection = cardSecurityFile.securityInfos
            for (securityInfo in securityInfoCollection) {
                if (securityInfo is PACEInfo) {
                    val paceInfo = securityInfo
                    passService.doPACE(
                        bacKey,
                        paceInfo.objectIdentifier,
                        PACEInfo.toParameterSpec(paceInfo.parameterId),
                        null
                    )
                    paceSucceeded = true
                }
            }
        } catch (e: Exception) {
            return null
        }

        passService.sendSelectApplet(paceSucceeded)

        if (!paceSucceeded) {
            try {
                passService.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                passService.doBAC(bacKey)
            }
        }

        // -- Personal Details -- //

        // -- Personal Details -- //
        val dg1In: CardFileInputStream = passService.getInputStream(PassportService.EF_DG1)
        val dg1File = DG1File(dg1In)

        val mrzInfo = dg1File.mrzInfo
        val name = mrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
        val surname = mrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' }
        val birthdate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(mrzInfo.dateOfBirth)?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
            } ?: "Empty field"
        return Person(name, surname, birthdate)
    }
}